
# Branch Predictor

Branch Prediction is a very important trick in Out of Order Processors which increases the performance of OOO processors.\
To know more about Branch Predictors refer : [Branch Predictors](https://www.dropbox.com/s/14haxwqad5fxbhh/chapt-3.pdf?dl=0)

This project implements 4 branch predictors using different Predictor sizes.

i) Predictor2400.java - 2400 bits \
ii) Predictor6400.java - 6400 bits \
iii) Predictor9999.java - 9999 bits \
iv) Predictor32000.java - 32000 bits. 



## References

 - www.cse.iitd.ac.in/srsarangi/files/papers/patmospaper.pdf
 - [Dynamic Branch predictors with perceptrons](https://www.cs.utexas.edu/~lin/papers/hpca01.pdf)
 - [Seznec A. A case for partially-tagged geometric historylength predictors](https://jilp.org/vol8/v8paper1.pdf)
 

## Run Locally

Clone the project

```bash
  git clone https://github.com/cs5190435/Adv.-Computer-Architecture/tree/main/A1
```

Go to the project directory

```bash
  cd Adv.-Computer-Architecture/A1
```

Create all executables

```bash
  ant clean
  ant 
  ant make-jar
```

Run the predictor of your choice. 

```bash
  java -jar jar/BranchPredictor.jar traces/trace1 2400
```

This runs the simulator with the input trace as trace1.\
It also tells the simulator that the maximum allowed predictor size is 2400 bits.\
We can change the maximum allowed predictor size to other 3 values and corresponding predictor is chosen and executed.

