package net.astropvp.alts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Shared MariaDB storage for the whole network (main + lobbies):
 *  astro_alts_players  one row per account: latest name, rank, last seen, server it is online on (NULL = offline)
 *  astro_alts_ips      one row per (account, IP) with first/last use; only the newest N IPs per account are kept
 *  astro_alts_bans     names banned on a server (each server publishes its own ban list)
 *  astro_alts_alerts   staff alerts, picked up by every server's poll
 * Mutes are read from AstroMute's table astro_mutes (read only).
 * All methods are called from one background thread.
 */
final class AltStore {

    static final class Account {
        final UUID uuid;
        final String name;
        final String rank;
        final long lastSeen;
        final String onlineServer;
        boolean banned;
        boolean muted;

        Account(ResultSet rs) throws SQLException {
            this.uuid = UUID.fromString(rs.getString("uuid"));
            this.name = rs.getString("name");
            this.rank = rs.getString("rank_name");
            this.lastSeen = rs.getLong("last_seen");
            this.onlineServer = rs.getString("online_server");
        }
    }

    static final class IpUse {
        final String ip;
        final long firstSeen;
        final long lastSeen;
        final int accounts;

        IpUse(String ip, long firstSeen, long lastSeen, int accounts) {
            this.ip = ip;
            this.firstSeen = firstSeen;
            this.lastSeen = lastSeen;
            this.accounts = accounts;
        }
    }

    private final String url;
    private final String user;
    private final String password;
    private Connection connection;

    AltStore(String host, int port, String database, String user, String password) {
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&characterEncoding=utf8";
        this.user = user;
        this.password = password;
    }

    private Connection connection() throws SQLException {
        if (this.connection == null || !this.connection.isValid(2)) {
            close();
            this.connection = DriverManager.getConnection(this.url, this.user, this.password);
        }
        return this.connection;
    }

    void close() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException ignored) {
                // reopened on next use
            }
            this.connection = null;
        }
    }

    void createTables() throws SQLException {
        try (Statement st = connection().createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS astro_alts_players ("
                    + " uuid CHAR(36) NOT NULL PRIMARY KEY, name VARCHAR(16) NOT NULL, name_lower VARCHAR(16) NOT NULL,"
                    + " rank_name VARCHAR(32) NULL, last_seen BIGINT NOT NULL, online_server VARCHAR(32) NULL,"
                    + " INDEX idx_alts_name (name_lower)) DEFAULT CHARSET=utf8");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS astro_alts_ips ("
                    + " uuid CHAR(36) NOT NULL, ip VARCHAR(45) NOT NULL, first_seen BIGINT NOT NULL, last_seen BIGINT NOT NULL,"
                    + " PRIMARY KEY (uuid, ip), INDEX idx_alts_ip (ip)) DEFAULT CHARSET=utf8");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS astro_alts_bans ("
                    + " server VARCHAR(32) NOT NULL, name_lower VARCHAR(16) NOT NULL,"
                    + " PRIMARY KEY (server, name_lower)) DEFAULT CHARSET=utf8");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS astro_alts_alerts ("
                    + " id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, uuid CHAR(36) NOT NULL, created BIGINT NOT NULL,"
                    + " message VARCHAR(1024) NOT NULL, INDEX idx_alerts_uuid (uuid, created)) DEFAULT CHARSET=utf8");
        }
    }

    /** Records a join: account row (online here) + this IP, then trims old IPs. */
    void recordJoin(UUID uuid, String name, String rank, String ip, String server, long now, int maxIps) throws SQLException {
        Connection c = connection();
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO astro_alts_players"
                + " (uuid, name, name_lower, rank_name, last_seen, online_server) VALUES (?, ?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE name = VALUES(name), name_lower = VALUES(name_lower),"
                + " rank_name = COALESCE(VALUES(rank_name), rank_name), last_seen = VALUES(last_seen), online_server = VALUES(online_server)")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            ps.setString(3, name.toLowerCase(Locale.ENGLISH));
            ps.setString(4, rank);
            ps.setLong(5, now);
            ps.setString(6, server);
            ps.executeUpdate();
        }
        if (ip != null) {
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO astro_alts_ips (uuid, ip, first_seen, last_seen)"
                    + " VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE last_seen = VALUES(last_seen)")) {
                ps.setString(1, uuid.toString());
                ps.setString(2, ip);
                ps.setLong(3, now);
                ps.setLong(4, now);
                ps.executeUpdate();
            }
        }
        List<String> old = new ArrayList<String>();
        try (PreparedStatement ps = c.prepareStatement("SELECT ip FROM astro_alts_ips WHERE uuid = ? ORDER BY last_seen DESC")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                int n = 0;
                while (rs.next()) {
                    if (++n > maxIps) {
                        old.add(rs.getString(1));
                    }
                }
            }
        }
        for (String o : old) {
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM astro_alts_ips WHERE uuid = ? AND ip = ?")) {
                ps.setString(1, uuid.toString());
                ps.setString(2, o);
                ps.executeUpdate();
            }
        }
    }

    void recordQuit(UUID uuid, String server, long now) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "UPDATE astro_alts_players SET online_server = NULL, last_seen = ? WHERE uuid = ? AND online_server = ?")) {
            ps.setLong(1, now);
            ps.setString(2, uuid.toString());
            ps.setString(3, server);
            ps.executeUpdate();
        }
    }

    /** After a restart nobody is online here any more. */
    void clearOnline(String server) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "UPDATE astro_alts_players SET online_server = NULL WHERE online_server = ?")) {
            ps.setString(1, server);
            ps.executeUpdate();
        }
    }

    /** Most recently seen account with that name, or null. */
    Account findByName(String name) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "SELECT * FROM astro_alts_players WHERE name_lower = ? ORDER BY last_seen DESC LIMIT 1")) {
            ps.setString(1, name.toLowerCase(Locale.ENGLISH));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new Account(rs) : null;
            }
        }
    }

    /** Every other account that used one of this account's stored IPs, with banned/muted filled in. */
    List<Account> alts(UUID uuid) throws SQLException {
        Map<UUID, Account> found = new LinkedHashMap<UUID, Account>();
        try (PreparedStatement ps = connection().prepareStatement("SELECT DISTINCT p.* FROM astro_alts_ips a"
                + " JOIN astro_alts_ips b ON b.ip = a.ip AND b.uuid <> a.uuid"
                + " JOIN astro_alts_players p ON p.uuid = b.uuid"
                + " WHERE a.uuid = ? ORDER BY p.last_seen DESC")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Account acc = new Account(rs);
                    found.put(acc.uuid, acc);
                }
            }
        }
        List<Account> list = new ArrayList<Account>(found.values());
        markStatus(list, System.currentTimeMillis());
        return list;
    }

    void markStatus(Collection<Account> accounts, long now) throws SQLException {
        if (accounts.isEmpty()) {
            return;
        }
        Set<String> banned = bannedNames();
        Set<UUID> muted = mutedUuids(now);
        for (Account acc : accounts) {
            acc.banned = banned.contains(acc.name.toLowerCase(Locale.ENGLISH));
            acc.muted = muted.contains(acc.uuid);
        }
    }

    Set<String> bannedNames() throws SQLException {
        Set<String> names = new HashSet<String>();
        try (Statement st = connection().createStatement();
             ResultSet rs = st.executeQuery("SELECT DISTINCT name_lower FROM astro_alts_bans")) {
            while (rs.next()) {
                names.add(rs.getString(1));
            }
        }
        return names;
    }

    /** Active mutes from AstroMute's table; empty if AstroMute never ran on this database. */
    Set<UUID> mutedUuids(long now) {
        Set<UUID> uuids = new HashSet<UUID>();
        try (PreparedStatement ps = connection().prepareStatement(
                "SELECT uuid FROM astro_mutes WHERE active = 1 AND (expires = 0 OR expires > ?)")) {
            ps.setLong(1, now);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        uuids.add(UUID.fromString(rs.getString(1)));
                    } catch (IllegalArgumentException ignored) {
                        // not a uuid
                    }
                }
            }
        } catch (SQLException missingTable) {
            // no astro_mutes table: nobody is muted
        }
        return uuids;
    }

    /** Replaces this server's published ban list. */
    void publishBans(String server, Collection<String> namesLower) throws SQLException {
        Connection c = connection();
        boolean auto = c.getAutoCommit();
        c.setAutoCommit(false);
        try {
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM astro_alts_bans WHERE server = ?")) {
                ps.setString(1, server);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement("INSERT IGNORE INTO astro_alts_bans (server, name_lower) VALUES (?, ?)")) {
                for (String n : namesLower) {
                    ps.setString(1, server);
                    ps.setString(2, n);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            c.commit();
        } catch (SQLException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(auto);
        }
    }

    List<IpUse> ips(UUID uuid) throws SQLException {
        List<IpUse> list = new ArrayList<IpUse>();
        try (PreparedStatement ps = connection().prepareStatement("SELECT a.ip, a.first_seen, a.last_seen,"
                + " (SELECT COUNT(DISTINCT b.uuid) FROM astro_alts_ips b WHERE b.ip = a.ip) AS accounts"
                + " FROM astro_alts_ips a WHERE a.uuid = ? ORDER BY a.last_seen DESC")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new IpUse(rs.getString(1), rs.getLong(2), rs.getLong(3), rs.getInt(4)));
                }
            }
        }
        return list;
    }

    boolean alertedSince(UUID uuid, long since) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "SELECT 1 FROM astro_alts_alerts WHERE uuid = ? AND created >= ? LIMIT 1")) {
            ps.setString(1, uuid.toString());
            ps.setLong(2, since);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    void addAlert(UUID uuid, String message, long now) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement(
                "INSERT INTO astro_alts_alerts (uuid, created, message) VALUES (?, ?, ?)")) {
            ps.setString(1, uuid.toString());
            ps.setLong(2, now);
            ps.setString(3, message);
            ps.executeUpdate();
        }
    }

    long lastAlertId() throws SQLException {
        try (Statement st = connection().createStatement();
             ResultSet rs = st.executeQuery("SELECT COALESCE(MAX(id), 0) FROM astro_alts_alerts")) {
            rs.next();
            return rs.getLong(1);
        }
    }

    /** Alerts newer than the given id, in order: id -> message. */
    Map<Long, String> alertsAfter(long id) throws SQLException {
        Map<Long, String> map = new LinkedHashMap<Long, String>();
        try (PreparedStatement ps = connection().prepareStatement(
                "SELECT id, message FROM astro_alts_alerts WHERE id > ? ORDER BY id")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getLong(1), rs.getString(2));
                }
            }
        }
        return map;
    }

    void pruneAlerts(long before) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement("DELETE FROM astro_alts_alerts WHERE created < ?")) {
            ps.setLong(1, before);
            ps.executeUpdate();
        }
    }
}
