package net.astropvp.gambling;

/** A bet typed in a command; parsed by the shared MoneyParser, so shorthand works (100k, 1.5m, 1b). */
public final class MoneyAmount {
    private final double value;
    private final String invalidInput;

    public MoneyAmount(double value) {
        this.value = value;
        this.invalidInput = null;
    }

    private MoneyAmount(String invalidInput) {
        this.value = 0;
        this.invalidInput = invalidInput;
    }

    /** typed text that isn't an amount (the framework would otherwise pass null, i.e. "no bet", and open the menu) */
    public static MoneyAmount invalid(String input) {
        return new MoneyAmount(input);
    }

    public boolean isInvalid() {
        return this.invalidInput != null;
    }

    public String getInput() {
        return this.invalidInput;
    }

    public double getValue() {
        return this.value;
    }
}
