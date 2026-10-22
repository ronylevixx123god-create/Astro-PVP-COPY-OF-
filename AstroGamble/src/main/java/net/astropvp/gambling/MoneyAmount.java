package net.astropvp.gambling;

/** A bet typed in a command; parsed by the shared MoneyParser, so shorthand works (100k, 1.5m, 1b). */
public final class MoneyAmount {
    private final double value;

    public MoneyAmount(double value) {
        this.value = value;
    }

    public double getValue() {
        return this.value;
    }
}
