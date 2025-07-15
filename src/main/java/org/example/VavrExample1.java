package org.example;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;
import static io.vavr.Predicates.isIn;

import io.vavr.CheckedFunction0;
import io.vavr.Function0;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import java.util.Random;
import java.util.logging.Logger;

public class VavrExample1 {

    private final static Logger LOGGER = Logger.getLogger(VavrExample1.class.getName());

    private static Function0<String> invalidMessage = () -> "Invalid command";
    private static Function0<String> webBasedMessage = () -> "Application started as web application";
    private static Function0<String> commandLineMessage = () -> "Application started as command line";


    public static void main(final String... args) {
        LOGGER.info(filterMessage(args));
        useOfCasePattern();
        useOfTry();
        LOGGER.info("" + useOfEither("8").isLeft());
        LOGGER.info("" + useOfEither(8).isLeft());
        useOfTry2();
        Either<String, Integer> parsed = parseInt("123");
        parsed.peek(i -> System.out.println("Parsed: " + i));
        tuple();
        retry();
    }

    public static String filterMessage(final String... args) {
        final Option<String> arg = Array.of(args)
                .headOption();
        return Match(arg.getOrElse(""))
                .of(Case($(isIn("-w", "--web")), webBasedMessage.apply()),
                        Case($(isIn("-c", "--command")), commandLineMessage.apply()),
                        Case($(), invalidMessage.apply()));
    }

    public static void useOfCasePattern() {
        final Object input = 10;
        final String result = Match(input).of(
                Case($(is(1)), "One"),
                Case($(is(2)), "Two"),
                Case($(isIn(3, 4, 5)), "Three to Five"),
                Case($(), "Other")
        );
        LOGGER.info("result:- " + result);
    }

    public static void useOfTry() {
        Try.of(() -> {
            throw new ClassCastException("this is exception");
        }).onFailure(t -> LOGGER.info("ex: " + t.getMessage()));
    }

    public static void useOfTry2() {
        final Try<Integer> result = Try.of(() -> Integer.parseInt("123"));
        if (result.isSuccess()) {
            LOGGER.info("Success:- " + result.get());
        } else {
            LOGGER.info("Failure:- " + result.getCause().getMessage());
        }
        final String output = result.map(i -> "Value: " + i)
                .recover(NumberFormatException.class, ex -> "Error: " + ex.getMessage()).get();
        LOGGER.info("output: " + output);
    }

    public static Either<String, Integer> useOfEither(final Object value) {
        if (value instanceof Integer) {
            return Either.right((Integer) value);
        } else if (value instanceof String) {
            return Either.left(value.toString());
        }
        return Either.left("");
    }

    public static Either<String, Integer> parseInt(String str) {
        try {
            return Either.right(Integer.parseInt(str));
        } catch (NumberFormatException e) {
            return Either.left("Invalid number: " + str);
        }
    }

    public static void tuple() {
        final Tuple2<String, Integer> tuple = Tuple.of("John", 30);
        LOGGER.info("tuple._1: " + tuple._1);
        LOGGER.info("tuple._2: " + tuple._2);
        final Tuple2<String, Integer> updated = tuple.map(
                n -> n.toUpperCase(),
                a -> a + 1
        );
        LOGGER.info("updated: " + updated);
        final String result = tuple.apply((n, a) -> n + " is " + a + " years old");
        LOGGER.info("result: " + result);
    }

    public static void lazyStream() {
        final Stream<Integer> fib = Stream.iterate(new int[]{0, 1}, t -> new int[]{t[1], t[0] + t[1]})
                .map(t -> t[0]);
        final List<Integer> first10 = fib.take(10).toList();
        LOGGER.info("first10: " + first10);
    }

    public static void retry() {
        final CheckedFunction0<String> unreliableOperation = () -> {
            if (new Random().nextBoolean()) {
                return "Success!";
            } else {
                throw new RuntimeException("Temporary failure");
            }
        };
        final Try<String> result = Try.of(unreliableOperation)
                .recoverWith(RuntimeException.class, e ->
                        Try.of(unreliableOperation)
                                .recoverWith(RuntimeException.class, e2 ->
                                        Try.of(unreliableOperation)));
        LOGGER.info("retry: " + result.getOrElse("Failed after 3 attempts"));
    }

}
