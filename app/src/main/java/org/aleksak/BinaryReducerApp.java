package org.aleksak;

import org.aleksak.dto.Adder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BinaryReducerApp {

    private static final int MAXIMUM_BITS = 64;

    public static void main(String[] args) {
        String addendOne = "1000000111011011001";
        String addendTwo = "1110011111110000";

        System.out.println("First addend: " + addendOne);
        System.out.println("Second addend: " + addendTwo);

        if (isNotBinary(addendOne) || isNotBinary(addendTwo)) {
            throw new RuntimeException("Not allowed symbols entered. Only 0 or 1 symbols are allowed.");
        }

        List<Boolean> binaryAddendOne = parseStringValueToBinary(addendOne);
        List<Boolean> binaryAddendTwo = parseStringValueToBinary(addendTwo);

        List<Boolean> binaryAddResult = add(binaryAddendOne, binaryAddendTwo);
        String binarySum = parseBinaryToString(binaryAddResult);

        System.out.println("Result in binary: " + binarySum);
        System.out.println("Result in decimal: " + parseBinaryToDecimal(binaryAddResult));
    }

    private static boolean isNotBinary(String value) {
        return !value.matches("^[01]+$");
    }

    private static List<Boolean> parseStringValueToBinary(String value) {
        List<Boolean> digits = Arrays.stream(value.split(""))
                .map(digit -> digit.equals("1"))
                .collect(Collectors.toList());

        Collections.reverse(digits);

        List<Boolean> refinedDigits = IntStream.rangeClosed(0, (MAXIMUM_BITS - 1))
                .mapToObj(i -> ((digits.size() - 1) >= i) ? digits.get(i) : false)
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.reverse(refinedDigits);
        return refinedDigits;
    }

    private static List<Boolean> add(List<Boolean> binaryAddendOne,
                                     List<Boolean> binaryAddendTwo) {
        int counter = MAXIMUM_BITS - 1;
        boolean inputBitOne = binaryAddendOne.get(counter);
        boolean inputBitTwo = binaryAddendTwo.get(counter);
        boolean initialResidualInput = false;

        Adder initialAdder = new Adder(inputBitOne, inputBitTwo, initialResidualInput);
        List<Boolean> accumulator = new ArrayList<>();
        process(initialAdder, accumulator, binaryAddendOne, binaryAddendTwo, counter);

        Collections.reverse(accumulator);
        return accumulator;
    }

    private static void process(Adder adder,
                                List<Boolean> accumulator,
                                List<Boolean> binaryAddendOne,
                                List<Boolean> binaryAddendTwo,
                                int counter) {
        if (counter >= 0) {
            counter--;

            boolean sum = ((adder.addendOne() != adder.addendTwo()) && !adder.residual()) ||
                    ((adder.addendOne() && adder.addendTwo()) && adder.residual()) ||
                    (!(adder.addendOne() || adder.addendTwo()) && adder.residual());

            boolean residualOutput = (adder.addendOne() & adder.addendTwo()) ||
                    (adder.addendOne() & adder.residual()) ||
                    (adder.addendTwo() & adder.residual());

            accumulator.add(sum);

            if (counter >= 0) {
                Adder nextAdder = new Adder(
                        binaryAddendOne.get(counter),
                        binaryAddendTwo.get(counter),
                        residualOutput
                );

                process(nextAdder, accumulator, binaryAddendOne, binaryAddendTwo, counter);
            }
        }
    }

    private static String parseBinaryToString(List<Boolean> binaryAddResult) {
        return binaryAddResult.stream()
                .map(bit -> bit.equals(true) ? "1" : "0")
                .collect(Collectors.joining(""));
    }

    private static long parseBinaryToDecimal(List<Boolean> binary) {
        Collections.reverse(binary);

        return IntStream.rangeClosed(0, (binary.size() - 1))
                .mapToObj(i -> binary.get(i).equals(true) ? ((long) Math.pow(2, i)) : 0)
                .reduce(0L, Long::sum);
    }

}