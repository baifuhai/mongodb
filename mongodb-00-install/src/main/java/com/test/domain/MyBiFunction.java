package com.test.domain;

public interface MyBiFunction<T, U, R> {

	R apply(T t, U u) throws Exception;

}
