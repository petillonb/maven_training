package fr.lernejo;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SampleTest {

    private Sample sample = new Sample();


    @Test
    void factorial_of_5_should_produce_120() {
        int n = 5;
        int result = sample.fact(n);
        Assertions.assertThat(result).as("factorial of 5").isEqualTo(120) ;
    }

}
