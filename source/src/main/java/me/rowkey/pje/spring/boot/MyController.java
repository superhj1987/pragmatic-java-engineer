/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.rowkey.pje.spring.boot;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RestController
public class MyController {

    @RequestMapping("/")
    public String hello(HttpServletRequest request, HttpServletResponse response) {
        return "Hello World!";
    }

    @RequestMapping("/async/test")
    @ResponseBody
    public Callable<String> callable() {

        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(2000);
                return "Callable Result";
            }
        };
    }

    private Executor executor = Executors.newFixedThreadPool(200);

    @RequestMapping("/async/deferred")
    @ResponseBody
    public DeferredResult<String> quotes() {
        DeferredResult<String> deferredResult = new DeferredResult<String>(2000L);
        deferredResult.onCompletion(new Runnable() {
            @Override
            public void run() {
                System.out.println("Deferred Result done !!!");
            }
        });

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                deferredResult.setResult("Deferred Result");
            }
        });

        return deferredResult;
    }
}
