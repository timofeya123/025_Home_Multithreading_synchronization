package com.company;

public class Main {

    volatile static int i=0; // если не сделать её volatile

    public static void main(String[] args) throws Exception {
        Resource resource = new Resource(); //
        resource.i=5;

        MyThread myThread = new MyThread(); // Создает Поток - one
        myThread.setName("one");
        MyThread myThread2 = new MyThread(); // Создает Поток - two
        myThread2.setName("two");

        myThread.setResource(resource); // запихиваем ему ссылку на ресурс
        myThread2.setResource(resource);

        myThread.start(); // запуск потока
        myThread2.start(); // запуск потока

        myThread.join(); // запускает выбранный поток до смерти
        myThread2.join(); // запускает выбранный поток до смерти

        System.out.println(resource.i);
        System.out.println(resource.j);
    }
}

class MyThread extends Thread { // создать поток при помощи extends
    Resource resource;

    public void setResource(Resource resource){
        this.resource = resource;
    }

    @Override
    public void run(){
        resource.chengI(); // запускаем в ресурсе метод i++;
        resource.chengJ(); // запускаем в ресурсе метод i++;
    }
}

class Resource{
    int i;
    static int j=0;

    public int getI(){
        return i;
    }

    public synchronized void setI(int i){ //можно синхронизировать целый метод
        this.i = i;
    }

    public void chengI(){
        System.out.println(this.i);
        synchronized (this){ // не даст доступ другим потокам, пока не выполнится, тот который начал работу
            int i = this.i;
            if (Thread.currentThread().getName().equals("one")){ // если поток one
                Thread.yield(); // дать возможность запустится другому потоку
            }
            i++;
            this.i=i;
        }
    }

    public static void chengJ(){ // никогда не смешивать статичекую и обычную синхронизацию
        System.out.println(Resource.j);
        synchronized (Resource.class){ // не даст доступ другим потокам, пока не выполнится, тот который начал работу
            while (Main.i<5){ // если Main.i не сделать volatile, то это место будет закешированно, и Main.i всегда будет =0
                int j = Resource.j;
                if (Thread.currentThread().getName().equals("one")){ // если поток one
                    Thread.yield(); // дать возможность запустится другому потоку
                }
                j++;
                Resource.j= j;
                Main.i++;
            }
        }
    }

}