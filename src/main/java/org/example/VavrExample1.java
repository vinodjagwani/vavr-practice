package org.example;

import io.vavr.API;
import io.vavr.Function0;
import io.vavr.collection.Array;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.util.logging.Logger;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.isIn;

public class VavrExample1 {

    private final static Logger LOGGER = Logger.getLogger(VavrExample1.class.getName());

    private static Function0<String> invalidMessage = () -> "Invalid command";
    private static Function0<String> webBasedMessage = () -> "Application started as web application";
    private static Function0<String> commandLineMessage = () -> "Application started as command line";


    public static void main(final String... args) {
        LOGGER.info(filterMessage(args));
        useOfTry();
        LOGGER.info("" + useOfEither("8").isLeft());
        LOGGER.info("" + useOfEither(8).isLeft());
    }

    public static String filterMessage(final String... args) {
        final Option<String> arg = Array.of(args).headOption();
        return API.Match(arg.getOrElse("")).of(
                Case($(isIn("-w", "--web")), webBasedMessage.apply()),
                Case($(isIn("-c", "--command")), commandLineMessage.apply()),
                Case($(), invalidMessage.apply()));
    }

    public static void useOfTry() {
        Try.of(() -> {
            throw new ClassCastException("this is exception");
        }).onFailure(t -> LOGGER.info("ex: " + t.getMessage()));
    }

    public static Either<String, Integer> useOfEither(final Object value) {
        if (value instanceof Integer)
            return Either.right((Integer) value);
        else if (value instanceof String)
            return Either.left(value.toString());
        return Either.left("");
    }
}
