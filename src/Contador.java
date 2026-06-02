import java.util.Scanner;

public class Contador implements Runnable {

    private int inicio;
    private int fin;
    private long tiempoEjecucion;

    public Contador(int inicio, int fin) {
        this.inicio = inicio;
        this.fin = fin;
    }

    @Override
    public void run() {
        long inicioTiempo = System.nanoTime();

        for (int i = inicio; i <= fin; i++) {
            System.out.println(i);
        }

        long finTiempo = System.nanoTime();
        tiempoEjecucion = (finTiempo - inicioTiempo) / 1_000_000;
    }

    public long getTiempoEjecucion() {
        return tiempoEjecucion;
    }

    public int getInicio() {
        return inicio;
    }

    public int getFin() {
        return fin;
    }

    public static void main(String[] args)
            throws InterruptedException {

        Scanner sc = new Scanner(System.in);

        System.out.print("Ingrese el límite: ");
        int limite = sc.nextInt();

        System.out.print("Ingrese la cantidad de hilos: ");
        int numHilos = sc.nextInt();

        Thread[] hilos = new Thread[numHilos];
        Contador[] contadores = new Contador[numHilos];

        int rango = limite / numHilos;

        long inicioGeneral = System.nanoTime();

        for (int i = 0; i < numHilos; i++) {

            int inicioRango = i * rango + 1;

            int finRango = (i == numHilos - 1)
                    ? limite
                    : (i + 1) * rango;

            contadores[i] = new Contador(inicioRango, finRango);
            hilos[i] = new Thread(contadores[i], "Hilo-" + (i + 1));
        }

        for (Thread hilo : hilos) {
            hilo.start();
        }

        for (Thread hilo : hilos) {
            hilo.join();
        }

        long finGeneral = System.nanoTime();

        System.out.println("\nRESULTADOS:");

        for (int i = 0; i < numHilos; i++) {

            System.out.println(
                    hilos[i].getName()
                            + " | Rango: "
                            + contadores[i].getInicio()
                            + " - "
                            + contadores[i].getFin()
                            + " | Tiempo: "
                            + contadores[i].getTiempoEjecucion()
                            + " ms"
            );
        }

        System.out.println(
                "\nTiempo total: "
                        + ((finGeneral - inicioGeneral) / 1_000_000)
                        + " ms"
        );

        sc.close();
    }
}

