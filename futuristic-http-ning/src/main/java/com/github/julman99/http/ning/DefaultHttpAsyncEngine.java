package com.github.julman99.http.ning;


import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.filter.*;
import com.pixable.utils.CollectionSizeLogger;
import com.pixable.utils.TimeDiffBuilder;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @autor: julio
 */
public class DefaultHttpAsyncEngine {

    private static AsyncHttpClientConfig DEFAULT_CONFIG = new AsyncHttpClientConfig.Builder().build();

    public static HttpAsyncEngine createNew(int threads, String name){
        return createNew(threads, name, null);
    }

    public static HttpAsyncEngine createNew(int threads, String name, CollectionSizeLogger logger){

        int ioThreadMultiplier = calculateIoMultiplier(threads);

        final ConcurrentLinkedQueue<Object> dummyStack = new ConcurrentLinkedQueue<>();

        AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder()
                .setIOThreadMultiplier(ioThreadMultiplier)
                .addRequestFilter(new RequestFilter() {
                    @Override
                    public FilterContext filter(FilterContext ctx) throws FilterException {
                        dummyStack.add(new Object());
                        return ctx;
                    }
                })
                .addResponseFilter(new ResponseFilter() {
                    @Override
                    public FilterContext filter(FilterContext ctx) throws FilterException {
                        dummyStack.poll();
                        return ctx;
                    }
                })
                .addIOExceptionFilter(new IOExceptionFilter() {
                    @Override
                    public FilterContext filter(FilterContext ctx) throws FilterException {
                        dummyStack.poll();
                        return ctx;
                    }
                })
                .setConnectionTimeoutInMs((int) new TimeDiffBuilder().addSeconds(15).getInMillis())
                .setRequestTimeoutInMs((int) new TimeDiffBuilder().addSeconds(15).getInMillis())
                .setAllowPoolingConnection(false)
                .build();



        AsyncHttpClient client = new AsyncHttpClient(config);


        if(logger != null){
            logger.add(name + " Concurrent", dummyStack);
        }

        return new NingHttpAsyncEngine(client);

    }

    /**
     * Since ning http does not have a way to setup the io thread count but only a multiplier,
     * we need to calculate the appropiate multiplier.
     *
     * The formula they use is multiplier * numberOfCpuCores. We will adjust the multiplier
     * by getting what is the default multiplier on the system and how many cores it has. With
     * that info we can estimate what the multiplier should be to reach at least the ioThreads
     * requested in the parameter
     * @param ioThreads
     * @return
     */
    private static int calculateIoMultiplier(int ioThreads){
        int processors = Runtime.getRuntime().availableProcessors();
        int defaultMultiplier = DEFAULT_CONFIG.getIoThreadMultiplier();
        int defaultIoThreads = processors * defaultMultiplier;
        double ratio = (double)ioThreads / (double)defaultIoThreads;
        int newMultiplier = (int) Math.ceil((double)defaultMultiplier * ratio);
        return newMultiplier;
    }

}
