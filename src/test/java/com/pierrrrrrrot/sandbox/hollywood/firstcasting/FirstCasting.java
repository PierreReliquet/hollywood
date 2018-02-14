package com.pierrrrrrrot.sandbox.hollywood.firstcasting;

import com.pierrrrrrrot.sandbox.hollywood.Hollywood;
import org.junit.Test;

import java.io.IOException;

public class FirstCasting {

    @Test
    public void automaticInitializationOfTheFirstCasting() throws IOException {
        Hollywood hollywood = new Hollywood();
        hollywood.initialize(FirstCasting.class.getResourceAsStream("configuration.json"));

        System.out.println(hollywood.get(Movie.class).firstActor);
    }

}
