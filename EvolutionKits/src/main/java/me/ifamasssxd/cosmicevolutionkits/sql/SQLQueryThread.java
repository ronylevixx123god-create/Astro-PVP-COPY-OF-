/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package me.ifamasssxd.cosmicevolutionkits.sql;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import me.ifamasssxd.cosmicevolutionkits.CosmicEvolutionKits;
import org.bukkit.Bukkit;

public class SQLQueryThread
implements Runnable {
    private volatile CopyOnWriteArrayList<String> querys = new CopyOnWriteArrayList();

    public void addQuery(String query) {
        this.querys.add(query);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        if (this.querys.isEmpty()) {
            return;
        }
        StringBuilder executed = new StringBuilder();
        try {
            PreparedStatement statement = CosmicEvolutionKits.get().getSqlDatabase().getConnection().prepareStatement("");
            try {
                for (String query : this.querys) {
                    statement.addBatch(query);
                    executed.append(query).append(", ");
                    this.querys.remove(query);
                }
                statement.executeBatch();
            }
            finally {
                if (Collections.singletonList(statement).get(0) != null) {
                    statement.close();
                }
            }
        }
        catch (Exception e) {
            Bukkit.getLogger().info("Error while executing queries: " + executed.toString());
            e.printStackTrace();
        }
    }
}

