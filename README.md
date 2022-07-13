# Equity Tech
Code to Connect 2022, Bank of America.

# Getting Started
## Pre-requisites
1. Java 11 (and above)
2. Maven

## Running the program 
1. Create a new folder
2. With this new folder as the working directory, clone this repository with the following terminal command:
```bash
git clone https://github.com/jamesyeap/equity_tech.git
```
3. `cd` into the repository folder
4. Build the program with the following command:
```bash
mvn package
```
   - this will create a new folder named "target" containing the compiled JAR program.
   - the compiled JAR program is named "equity-tech-1.0" 
5. `cd` into the "target" folder
6. Run the program with the following command:
```bash
java -jar equity-tech-1.0
```
7. Enter a POV BUY order in FIX format:
- For example, to submit an order with the following parameters:
  - order side: BUY
    - FIX tag: `54=1`
  - order type: MARKET order
    - FIX tag: `40=1`
  - order quantity: 50,000 shares
    - FIX tag: `38=50000`
  - target percentage: 10%
    - FIX tag: `6404=10`
- Enter the following FIX input:
  - `54=1; 40=1; 38=50000; 6404=10`
8. Enter the relative file-path of a market-data input file
  - a sample CSV file is available in the repository folder:
    - `<REPOSITORY_FOLDER>/input/market_data.zip`
  - to use the file, enter the file-path when prompted:
      - `../input/market_data.csv`
  - note: if you are using a custom market-data input file, it must be
    - a CSV file and,
    - conforms to the market-data input format
      - use the provided market-data input file as reference
9. If you wish to split buy-orders in batches, enter `y` when prompted and enter the batch size
   - For example, if you wish to ensure that all buy-orders greater than 500 shares are split into multiple orders
     - enter `500`
   - If you do not wish to split buy-orders, enter `n` when prompted
10. The simulation will now begin.

## Saving output
To save the execution logs of the program, run the program with the following command:
```bash
java -jar equity-tech-1.0 > OUTPUT_LOG.txt
```
  - this will save all logs to a text-file named "OUTPUT_LOG.txt" in the current working directory.