package org.example;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

public class HttpErrorCaptureAgent {

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        new AgentBuilder.Default()
                .type(ElementMatchers.any())
                .transform((builder, typeDescription, classLoader, module, Pro) ->
                        builder.method(ElementMatchers.namedOneOf("executeActivity"))
                                .intercept(Advice.to(HttpErrorInterceptor.class))
                )
                .installOn(instrumentation);
    }

    public static class HttpErrorInterceptor {
        @Advice.OnMethodEnter()
        public static void intercept(@Advice.Origin Method method) {
            System.err.println("intercept.. ");
        }
        @Advice.OnMethodExit(onThrowable = Throwable.class)
        public static void intercept(@Advice.Origin Method method, @Advice.Thrown Throwable throwable) {
            System.err.println("HTTP error occurred in method ");
        }

        public static boolean isHttpCommunicationMethod(Method method) {
            // Add logic to determine if the method is related to HTTP communication
            // For example, check if the method is from a servlet or an HTTP client library
            // You can use method.getName(), method.getDeclaringClass(), etc. for identification
            return true;
        }

        public static boolean isHttpError(Throwable throwable) {
            // Add logic to determine if the throwable represents an HTTP error
            // For example, check if the throwable is an instance of IOException or ServletException
            return true;
        }
    }
}
