class Program {
    # Scenario 1
    a: int <- a; # OK; attributes can self-reference for their auto-initialized value (a = 0)
    # Scenario 2
    b: int <- c; # NOT OK; attributes cannot forward-reference other attributes
    c: int <- 1;
    # Scenario 3
    foo(): int {
        x: int <- y; # OK; methods can forward-reference attributes (x = 2)
        this.bar(x) # OK; methods can forward-reference other methods (returns 3)
    }
    bar(x: int): int {
        x + 1
    }
    y: int <- 2;
    # Scenario 4
    zap(): int {
        x: int <- x; # OK; locals can self-reference for their auto-initialized value (x = 0)
        x
    }
    # Scenario 5
    g: int <- 3;
    qaz(): int {
        g: int <- g; # OK; locals will prefer to reference existing attributes over self-referencing (g = 3)
        g
    }
    # Scenario 5.5
    qaz2(): int {
        # Same as scenario 5, but clarifying that methods can still
        # forward-reference attributes even in this strange edge case
        g2: int <- g2; # OK; (g = 3)
        g2
    }
    g2: int <- 3;
    # Scenario 6
    bang(): int {
        u: int <- k; # NOT OK; locals cannot forwards-reference other locals
        k: int <- 4;
        u
    }
}