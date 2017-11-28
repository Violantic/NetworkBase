/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package com.nemegaming.playervaults.database;

/**
 * Created by Ethan on 2/24/2017.
 */
public interface Callback<T> {

    void call(T callback);

}
