package fr.lernejo;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


class SampleTest {

    private final Sample sample = new Sample();


    @Test
    void factorial_of_5_should_produce_120() {
        int n = 5;
        int result = sample.fact(n);
        Assertions.assertThat(result).as("factorial of 5").isEqualTo(120) ;
    }

    @Test
    void factorial_negative_int_should_produce_exeption(){
        int n = -5;
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .as("Negative int Fact")
            .isThrownBy(() -> sample.fact(n));
    }
    @Test
    void operation_addition_of_5_and_10_should_produce_15() {
        int a = 5;
        int b = 10;
        int result = sample.op(Sample.Operation.ADD,a,b);
        Assertions.assertThat(result).as("10+5").isEqualTo(15);
    }
    @Test
    void operation_multiplication_of_5_and_10_should_produce_50() {
        int a = 5;
        int b = 10;
        int result = sample.op(Sample.Operation.MULT,a,b);
        Assertions.assertThat(result).as("10*5").isEqualTo(50);

    }

}
