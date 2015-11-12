import java.lang.Math;
import java.util.Random;

public class geneticProg {


		//TinyVM myVM = new TinyVM();
		int populationSize = 100;
		double mutationRate = 0.1;
		double crossoverRate = 0.5;
		int numberOfGenerations;
		//int *primeNumber;
		int numberOfPrimes = 168;
		public static final int PROTECT_BEST = 5;
		public double[] primeNumber = new double[numberOfPrimes];
		TinyVM[] myVM = new TinyVM[populationSize];
		
	    static Random rand = new Random(42);



		/**
		 * Returns a pseudo-random number between min and max, inclusive.
		 * The difference between min and max can be at most
		 * <code>Integer.MAX_VALUE - 1</code>.
		 *
		 * @param min Minimum value
		 * @param max Maximum value.  Must be greater than min.
		 * @return Integer between min and max, inclusive.
		 * @see java.util.Random#nextInt(int)
		 */
		public static int randInt(int min, int max) {

		    // NOTE: This will (intentionally) not run as written so that folks
		    // copy-pasting have to think about how to initialize their
		    // Random instance.  Initialization of the Random instance is outside
		    // the main scope of the question, but some decent options are to have
		    // a field that is initialized once and then re-used as needed or to
		    // use ThreadLocalRandom (if using at least Java 1.7).

		    // nextInt is normally exclusive of the top value,
		    // so add 1 to make it inclusive
		    int randomNum = rand.nextInt((max - min) + 0) + min;

		    return randomNum;
		}

		/**********************************************************************
		* \brief: constructor: initializes variables and generates random
		*         programs and computes a number of primes
		**********************************************************************/
		public geneticProg(double mutationRate, double crossoverRate, int populationSize, int numberOfPrimes) {
			this.populationSize = populationSize;
			this.numberOfPrimes = numberOfPrimes;
			this.mutationRate = mutationRate;
			this.crossoverRate = crossoverRate;
			
			// Ininialize Individuums
			for (int individuum=0; individuum<populationSize; individuum++){
				for (int t=0;t<TinyVM.PROG_LENGTH;t++){
					TinyVM.memory[t] = randInt(0, Integer.MAX_VALUE); // random programs
				}
				for (int t=TinyVM.SUBROUTINE_SIZE; t<TinyVM.PROG_LENGTH; t+=TinyVM.SUBROUTINE_SIZE){
					TinyVM.memory[t-1] = TinyVM.Commands.RET.ordinal(); // end of subroutines
				}
			}
			// compute prime numbers
			int counter;
			counter = 0;
			for (int t=2;t<1000000;t++){
				double SQR = Math.sqrt((double)t);
				boolean prime = true;
				for (int r=2; r<=SQR; r++){
					if ((t % r) == 0){
						prime = false;
						r = (int) SQR;
					}
				}
				if (prime){
					primeNumber[counter++] = t;
					System.out.format("%d,",t);
				}
				if (counter >= (numberOfPrimes-1)){
					System.out.format("\n");
					break;
				}
			}

		}

		/**********************************************************************
		* \brief: evaluates the fitness of all individuals
		**********************************************************************/
		void evaluateFitness(int individuum)
		{
			for (int zz=0;zz<100;zz++){
				myVM[individuum].primeA = primeNumber[randInt(0, Integer.MAX_VALUE)%numberOfPrimes];
				myVM[individuum].primeB = primeNumber[randInt(0, Integer.MAX_VALUE)%numberOfPrimes];

				int cycles = myVM[individuum].simulate();
			}
		}
		/**********************************************************************
		* \brief: sorts the individuals by fitness
		**********************************************************************/
		void selectBestIndividuals()
		{
			// sort individuals for fitness
			for (int t=0;t<populationSize;t++){
				for (int i=t+1;i<populationSize;i++){
					if (myVM[t].fitness < myVM[i].fitness){
						double help;
						help = myVM[t].fitness;
						myVM[t].fitness = myVM[i].fitness;
						myVM[i].fitness = help;
						for (int j=0; j<TinyVM.PROG_LENGTH; j++){
							int mem;
							mem = myVM[t].memory[j];
							myVM[t].memory[j] = myVM[i].memory[j];
							myVM[i].memory[j] = mem;
						} // endfor
					}// endif
				}// endfor i
			}// endfor t
		}
		/**********************************************************************
		* \brief: half of the individuals of the next generation are produced
		*         by crossover...
		**********************************************************************/
		void crossover()
		{
			int crossoverPoint, loverIndividuum;
			// --- better half generates children into the worse half
			for (int t=0;t<populationSize*crossoverRate;t++){
				//myVM[t+populationSize/2].fitness = myVM[t].fitness;

				crossoverPoint  =  (randInt(0, Integer.MAX_VALUE)%TinyVM.PROG_LENGTH * TinyVM.SUBROUTINE_SIZE) % TinyVM.PROG_LENGTH;
				loverIndividuum =  randInt(0, Integer.MAX_VALUE) % populationSize; // gets children with random

				// half of selected are build via crossover

				for (int j=0;j<TinyVM.PROG_LENGTH;j++){
					if (j<crossoverPoint){
						myVM[(int)(t+populationSize*(1.0-crossoverRate))].memory[j] = myVM[t].memory[j]; // mother gene
					}
					else{
						myVM[(int)(t+populationSize*(1.0-crossoverRate))].memory[j] = myVM[loverIndividuum].memory[j]; // father gene
					}
				}
			}
		}
		/**********************************************************************
		* \brief: sets all fitness values to zero
		**********************************************************************/
		void resetFitness()
		{
			for (int t=0;t<populationSize;t++){
				myVM[t].fitness = 0;
			}
		}
		/**********************************************************************
		* \brief: mutates some of the individuals dependent on the mutation rate
		**********************************************************************/
		void mutate()
		{
			for (int t=0; t<populationSize * mutationRate; t++){ // 10% mutation
				myVM[randInt(0, Integer.MAX_VALUE)%(populationSize-PROTECT_BEST)+PROTECT_BEST].memory[(randInt(0, Integer.MAX_VALUE)%TinyVM.PROG_LENGTH)] = randInt(0, Integer.MAX_VALUE); // Mutate, but save the best 5
			}
			// over all individuums: inshure that "return from subroutine" is not mutated
			for (int individuum=0; individuum<populationSize; individuum++){
				for (int t=TinyVM.SUBROUTINE_SIZE; t<TinyVM.PROG_LENGTH; t+=TinyVM.SUBROUTINE_SIZE){
					myVM[individuum].memory[t-1]=TinyVM.Commands.RET.ordinal(); // end of subroutines
				}
			}
		}
		/**********************************************************************
		* \brief: The steering of the evolution process is done here
		**********************************************************************/
		double evolveGenerationAndGetBestFitness()
		{

			resetFitness();

			// over all individuums:
			for (int individuum=0; individuum<populationSize; individuum++){
				evaluateFitness(individuum);
			}

			selectBestIndividuals();
			crossover();
			mutate();

			return (double)myVM[0].fitness/100.0;
		}
		/**********************************************************************
		* \brief: This function enables the user to view what the evolved
		*         program does
		**********************************************************************/
		int simulateIndividuum(int individuum, boolean debug){
			individuum = 0;
			debug = true; 
			
			myVM[individuum%populationSize].debug = debug;
			int cycles = myVM[individuum%populationSize].simulate();
			myVM[individuum%populationSize].debug = false;
			return cycles;
		}
		

	public static void main(String[] args) {
		int MAX_PRIMES = 168;
		int MAX_GENERATIONS = 100;
		int MAX_POPULATION = 100;
		double MUTATION_RATE = 0.1;
		double CROSSOVER_RATE = 0.5;

			double bestFitness;
			//FILE* outFile;
			geneticProg myGP = new geneticProg(MUTATION_RATE, CROSSOVER_RATE, MAX_POPULATION, MAX_PRIMES);

			//outFile = fopen("fitness.csv","wb");

			//fprintf(outFile,"Generation;bestFitness\n");
			for (int generation = 1; generation<=MAX_GENERATIONS; generation++){
				bestFitness = myGP.evolveGenerationAndGetBestFitness();
				System.out.format("Generation %d fitness of best individuum = %f\n", generation, bestFitness);
				//fprintf(outFile,"%d;%f\n", generation, bestFitness);
				
			}
			//fclose(outFile);
			//myGP.simulateIndividuum(); // simulates best individuum

}
}

