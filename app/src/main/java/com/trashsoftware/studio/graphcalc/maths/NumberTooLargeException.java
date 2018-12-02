package com.trashsoftware.studio.graphcalc.maths;

public class NumberTooLargeException extends ArithmeticException {

    public NumberTooLargeException() {
        super();
    }

    public NumberTooLargeException(String message) {
        super(message);
    }
}
