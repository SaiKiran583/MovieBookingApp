package com.rbp.movieapp.exception;

public class SeatAlreadyBooked extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public SeatAlreadyBooked(String s) {
        super(s);
    }
}
