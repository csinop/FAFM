package com.example.fitnessappformyself.main_menu_fragments;

public interface AsyncResult<T> {
    void onTaskFinish(T result);
}
