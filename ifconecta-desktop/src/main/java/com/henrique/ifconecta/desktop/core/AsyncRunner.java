package com.henrique.ifconecta.desktop.core;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.concurrent.Task;

/**
 * Executa chamadas bloqueantes (serviços/API) fora da FX Application Thread
 * e entrega o resultado de volta nela — substitui o padrão async/await + loading
 * usado no front web.
 */
public final class AsyncRunner {

    private AsyncRunner() {
    }

    /**
     * @param work    trabalho bloqueante (ex.: chamada de serviço)
     * @param onOk    callback de sucesso, executado na FX thread
     * @param onError callback de erro, executado na FX thread
     */
    public static <T> void run(Supplier<T> work, Consumer<T> onOk, Consumer<Throwable> onError) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() {
                return work.get();
            }
        };
        task.setOnSucceeded(e -> onOk.accept(task.getValue()));
        task.setOnFailed(e -> onError.accept(task.getException()));

        Thread thread = new Thread(task, "ifc-async");
        thread.setDaemon(true);
        thread.start();
    }

    /** Variante para trabalho sem retorno (ex.: PUT/PATCH). */
    public static void runVoid(Runnable work, Runnable onOk, Consumer<Throwable> onError) {
        run(() -> {
            work.run();
            return null;
        }, ignored -> onOk.run(), onError);
    }
}
