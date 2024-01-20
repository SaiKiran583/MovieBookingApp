package com.rbp.movieapp.exception;

public class MoviesNotFound extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public MoviesNotFound(String noMoviesAreAvailable) {
        super(noMoviesAreAvailable);
    }
}
