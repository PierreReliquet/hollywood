package com.pierrrrrrrot.sandbox.hollywood.firstcasting;

public class PrivateActor {

    private FavoriteScenarist favoriteScenarist;

    private PrivateActor() {
        // Testing that the private will be made accessible
    }

    @Override
    public String toString() {
        return "I am a private actor!";
    }
}
