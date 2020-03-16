package com.daemonize.game;

import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;


@Daemon
public class CommandParser {

    private static String listCmd = "ls";


    private class NonEmptyStack<T> {
        private Stack<T> stack = new Stack();
        private T first;

        public NonEmptyStack(T firstElement) {
            first = firstElement;
        }

        public void push(T element) {
            stack.push(element);
        }

        public T pop() {
            if (!stack.empty())
                return stack.pop();
            else
                return first;
        }

        public T peek() {
            if (stack.empty())
                return first;
            else
                return stack.peek();
        }

        public T bottom() {
            return first;
        }

        public List<T> elements() {
            List ret = new ArrayList();

            ret.add(first);

            Enumeration<T> elems = stack.elements();

            while(elems.hasMoreElements()) {
                ret.add(elems.nextElement());
            }

            return ret;
        }

    }

    private Scanner cmdScanner;
    private NonEmptyStack<Pair<Object, String>> stack;

    public CommandParser(Object reflectiveObject) {
        this.cmdScanner = new Scanner(System.in);
        this.stack = new NonEmptyStack<>(Pair.create(reflectiveObject, "root"));
    }

    @SideQuest
    public void parse() {

        try {

            String cmd = cmdScanner.nextLine();

            if (cmd.equals("fields")) {
                Field[] fieldarray = stack.peek().getFirst().getClass().getDeclaredFields();

                for (Field field : fieldarray) {
                    System.out.println(DaemonUtils.tag() + field.getDeclaringClass().getSimpleName() +  " - Type: " + field.getType() + " - fieldname: " + field.getName());
                }

            } else if (cmd.startsWith("field ")) {

                String fieldName =  cmd.replace("field ", "");
                System.out.println(DaemonUtils.tag() + getField(fieldName).toString());

            } else if (cmd.equals("ret")) {
                stack.pop();
            } else if (cmd.startsWith("get ")) {

                String fieldname = cmd.replace("get ", "");

                Field field = stack.peek().getFirst().getClass().getDeclaredField(fieldname);
                field.setAccessible(true);

                Object o = field.get(stack.peek().getFirst());
                stack.push(Pair.create(o, field.getName()));

            } else if (cmd.startsWith("methods")) {

                Method[] methods = stack.peek().getFirst().getClass().getMethods();

                for (Method method : methods) {
                    System.out.println(DaemonUtils.tag() + stack.peek().getFirst().getClass().getSimpleName() + "->" + method.getName());
                }

            } else if (cmd.startsWith("method ")) {

                String methodName = cmd.replace("method ", "");

                Method[] methods = stack.peek().getFirst().getClass().getMethods();

                for (Method method : methods) {
                    if (method.getName().equals(methodName)) {

                        Class<?>[] params =  method.getParameterTypes();

                        System.out.println(DaemonUtils.tag() + stack.peek().getFirst().getClass().getSimpleName() + "/"
                                + stack.peek().getFirst().getClass().getSimpleName() + "-> "
                                + method.getReturnType().getSimpleName() + " " + method.getName());

                        for(Class<?> param : params) {
                            System.err.println("    Parameter:" + param.getSimpleName());
                        }
                    }
                }


            } else if(cmd.startsWith("toString")) {
                System.out.println(stack.peek().getFirst().toString());
            } else if (isMethod(cmd)) {
                Object ret = execStatement(cmd);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder path = new StringBuilder();

        List<Pair<Object, String>> elements = stack.elements();

        for(int i = 0; i < elements.size(); ++i) {

            if(i == elements.size() - 1) {
                path.append(elements.get(i).getSecond());
            } else {
                path.append(elements.get(i).getSecond() + ".");
            }
        }

        System.out.print(DaemonUtils.tag() + stack.peek().getFirst().getClass().getSimpleName() + "(" + path.toString() + ")>");
    }

    private Object getField(String fieldName) throws NoSuchFieldException, IllegalAccessException, InstantiationException {

        Object root;

        if(fieldName.equals("this")) {
            return stack.peek().getFirst();
        }

        if (fieldName.contains(".")) {

            if (fieldName.startsWith(".")) {
                root = stack.bottom().getFirst();
            } else {
                root = stack.peek().getFirst();
            }

            Object currentObject = root;

            if (fieldName.lastIndexOf(".") > 0) {


                fieldName = fieldName.replaceFirst("." , "");

                String[] path = fieldName.split("\\.");
                int cnt = 0;

                while (cnt < path.length) {
                    Field currentFiled = currentObject.getClass().getDeclaredField(path[cnt]);
                    currentFiled.setAccessible(true);
                    currentObject = currentFiled.get(currentObject);
                    cnt++;
                }

            } else {

                Field retField = root.getClass().getDeclaredField(fieldName.replace(".", ""));
                retField.setAccessible(true);

                if (retField.getType().isPrimitive()) {
                    if (retField.getType().equals(int.class)){
                        return retField.get(Integer.valueOf(0));
                    } else if (retField.getType().equals(float.class)) {
                        return retField.get(Float.valueOf(0));
                    } else if (retField.getType().equals(byte.class)) {
                        return retField.get(Byte.valueOf((byte)0x00));
                    } else if (retField.getType().equals(short.class)) {
                        return retField.get(Short.valueOf((short)0));
                    } else if (retField.getType().equals(long.class)) {
                        return retField.get(Long.valueOf((long)0));
                    } else if (retField.getType().equals(double.class)) {
                        return retField.get(Double.valueOf((double)0));
                    } else if (retField.getType().equals(char.class)) {
                        return retField.get(Character.valueOf((char)0));
                    }

                } else {
                    currentObject = retField.get(stack.bottom().getFirst());
                }

            }

            return currentObject;

        } else {

            Field retField = stack.peek().getFirst().getClass().getDeclaredField(fieldName);
            retField.setAccessible(true);
            Object ret = retField.get(stack.peek().getFirst());
            return ret;
        }

    }

    private boolean isMethod(String statement) {
        return  (statement.contains("(") && statement.contains(")"));
    }

    private Object execStatement(String statement)  throws NoSuchFieldException, IllegalAccessException, InstantiationException, InvocationTargetException {

        if(!isMethod(statement)) {
            return getField(statement);
        }

        Object ret = null;

        int openParenthesis = statement.indexOf('(');
        String methodPath = statement.substring(0, openParenthesis);

        Object field = null;
        String methodName = null;


        if(methodPath.contains(".") && !methodPath.contains("this")) {
            int lastDot = methodPath.lastIndexOf(".");
            String fieldName = methodPath.substring(0, lastDot);
            field = getField(fieldName);
            methodName = methodPath.substring(lastDot + 1);
        } else {
            field = stack.peek().getFirst();
            methodName = methodPath;
        }

        //args
        String argsString = statement.substring(openParenthesis + 1, statement.length() - 1);

        if (argsString.isEmpty()) { //method has no args


            //find method
            Method[] methods = field.getClass().getMethods();

            Method invoked = null;

            for (Method method : methods) {
                if (method.getName().equals(methodName) && method.getParameterTypes().length == 0) {
                    invoked = method;
                    break;
                }
            }

            if (invoked.getReturnType().equals(void.class)) {
                invoked.invoke(field);
            } else {
                ret = invoked.invoke(field);
            }

        } else {

            String[] argArray = null;
            List<Object> evaluatedArgs = new LinkedList<>();

            if (argsString.contains(",")) {
                argArray = argsString.split(",");
            } else if (!argsString.isEmpty()) { //one arg
                argArray = new String[]{argsString};
            } else if (argsString.isEmpty()) {
                argArray = new String[]{};
            }

            for (String arg : argArray) {

                arg = arg.trim();

                if (isMethod(arg)) {
                    evaluatedArgs.add(execStatement(arg));
                } else if (arg.equals("this")) {
                    evaluatedArgs.add(stack.peek().getFirst());
                } else if (arg.startsWith("l ")) {//TODO regexp literal args

                    arg = arg.substring(2);

                    if (arg.matches("[0-9.]*")) { // check if arg is a number

                        Number num;

                        try {
                            num = Integer.parseInt(arg);
                        } catch (NumberFormatException e) {
                            try {
                                num = Float.parseFloat(arg);
                            } catch (NumberFormatException ex) {
                                num = Double.parseDouble(arg); //could be double if user goes wild with decimals
                            }
                        }

                        evaluatedArgs.add(num);
                    } else {// store arg as a string
                        evaluatedArgs.add(arg);
                    }
                } else if (arg.equals("null")) {
                    evaluatedArgs.add(null);
                } else {
                    evaluatedArgs.add(getField(arg));
                }
            }

            //find method
            Method[] methods = field.getClass().getMethods();

            Method invoked = null;

            for (Method method : methods) {
                if (method.getName().equals(methodName) && (method.getParameterTypes().length == evaluatedArgs.size())) {
                    invoked = method;
                    break;
                }
            }

            ret = invoked.invoke(field, evaluatedArgs.toArray());
        }


        return ret;
    }

}
