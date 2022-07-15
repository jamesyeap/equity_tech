# Equity Tech
Code to Connect 2022, Bank of America.

# Getting Started
## Pre-requisites
1. Java 11 (and above)
2. Maven

## Running the program 
1. Create a new folder
2. `cd` into this new folder
3. Clone this repository with the following terminal command:
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
5. Run the program with the following command:
```bash
java -jar target/equity-tech-1.0
```
6. Enter a POV BUY order in FIX format:
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
7. Enter the relative file-path of a market-data input file
  - some sample CSV files is available in the repository folder:
    - `<REPOSITORY_FOLDER>/input`
  - to use the default input file, enter the file-path when prompted:
      - `./input/market_data.csv`
  - note: if you are using a custom market-data input file, it must be
    - a CSV file and,
    - conforms to the market-data input format
      - use the provided market-data input file as reference
    - for ease of access, place it in the repository folder:
      - `<REPOSITORY_FOLDER>/input`
      - then, enter the file-path when prompted:
        - `./input/<NAME_OF_YOUR_CUSTOM_DATA_FILE>.csv`
8. If you wish to split buy-orders in batches, enter `y` when prompted and enter the batch size
  - For example, if you wish to ensure that all buy-orders greater than 500 shares are split into multiple orders
    - enter `500`
  - If you do not wish to split buy-orders, enter `n` when prompted
9. The simulation will now begin.

## Output
The program will send logs to 2 locations:
- to the console
- save logs in files in the current working directory

### Console Logs
When the simulation is running, the console will display detailed information about the Market, Trading Engine and the Simulated Exchange:
```
===========================================================================================
=============================================== RECEIVED MARKET UPDATE AT 5595244 ms ======
==================================================== BEST-BID: 52.35 ======================
==================================================== BEST-ASK: 52.45 ======================
==================================================== MARKET-TRADED VOLUME: 351500 =======
===========================================================================================
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::: TRADING ENGINE ASSESSMENT: (PASSIVE POSTING) ::::::::::::::::::::::::::::::::::::::::
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
[N:52.35:50] Queued 50@52.35

[C:52.25:40] Cancelled 40@52.25

[C:52.25:18940] Cancelled 18940@52.25

[N:52.25:18930] Queued 18930@52.25
```
See sections below for the breakdown.

#### Market Information
The first part of the log displays information about the market.
```
===========================================================================================
=============================================== RECEIVED MARKET UPDATE AT 5595244 ms ======
==================================================== BEST-BID: 52.35 ======================
==================================================== BEST-ASK: 52.45 ======================
==================================================== MARKET-TRADED VOLUME: 351500 =======
===========================================================================================
```

#### Trading Engine Response
The second part of the log displays the trading engine's response to the latest market condition, which can be one of the following:
- PASSIVE POSTING
- BEHIND MIN-RATIO
- BREACHED MAX-RATIO
```
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::: TRADING ENGINE ASSESSMENT: (PASSIVE POSTING) ::::::::::::::::::::::::::::::::::::::::
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
```

#### Sent Orders
The last part of the log displays information about the order sent by the trading engine, and the simulated engine's execution.
```
[N:52.35:50] Queued 50@52.35

[C:52.25:40] Cancelled 40@52.25

[C:52.25:18940] Cancelled 18940@52.25

[N:52.25:18930] Queued 18930@52.25
```
- `[N:52.35:50]` indicates that the trading engine sent an order of `50@52.35` to the simulated exchange
- `Queued 50@52.35` indicates that the simulated exchange has placed this order in the queue.

#### Simulation Summary
At the end of the simulation, a summary of the order execution will be printed to the console:
```
*********************************************************************************************
************************************ [SIMULATION REPORT] ************************************
*********************************************************************************************
Volume-Weighted Average Price: 52.67968

Client Order Size: 50000
Client Order Size Filled: 50000
********************************************************************************************
```

### File Logs
At the end of the simulation, logs will be saved in two files in the current working-directory:
- `TradingEngineLogs.txt`
- `SimulatedExchangeLogs.txt`