package main

import (
	"fmt"
	"sync"
	"time"
)

type Contador struct {
	inicio int
	fin    int
	tiempo time.Duration
}

func (c *Contador) contar(wg *sync.WaitGroup) {
	defer wg.Done()

	inicioTiempo := time.Now()

	for i := c.inicio; i <= c.fin; i++ {
		fmt.Println(i)
	}

	c.tiempo = time.Since(inicioTiempo)
}

func main() {

	var limite int
	var numHilos int

	fmt.Print("Ingrese el límite: ")
	fmt.Scan(&limite)

	fmt.Print("Ingrese la cantidad de hilos: ")
	fmt.Scan(&numHilos)

	contadores := make([]*Contador, numHilos)

	rango := limite / numHilos

	var wg sync.WaitGroup

	inicioGeneral := time.Now()

	for i := 0; i < numHilos; i++ {

		inicioRango := i*rango + 1

		finRango := (i + 1) * rango
		if i == numHilos-1 {
			finRango = limite
		}

		contadores[i] = &Contador{
			inicio: inicioRango,
			fin:    finRango,
		}

		wg.Add(1)
		go contadores[i].contar(&wg)
	}

	wg.Wait()

	tiempoTotal := time.Since(inicioGeneral)

	fmt.Println("\nRESULTADOS:")

	for i, c := range contadores {
		fmt.Printf(
			"Goroutine-%d | Rango: %d-%d | Tiempo: %v\n",
			i+1,
			c.inicio,
			c.fin,
			c.tiempo,
		)
	}

	fmt.Printf("\nTiempo total: %v\n", tiempoTotal)
}
