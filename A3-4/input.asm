    MACRO
    ONE	&O, &N, &E=AREG
    MOVER	&E, &O
    ADD	&E, &N
    MOVEM	&E, &O
    MEND
    MACRO
    TWO	&T, &W, &O=DREG
    MOVER	&O, &T
    ADD	&O, &W
    MOVEM	&O, &T
    ONE	&W, 9
    MEND
    START
    READ	O
    READ	T
    TWO	T, 7
    PRINT	O
    PRINT	T
    STOP
O	DS	1
T	DS	1
    END