# invoice-generator-clj
Generates invoices and business statements in PDF based on inputted data.
My first ad-hoc Clojure project written back in 2015 

## Usage
Look at files in resources to know what kind of data points are accepted. Sample invoices can be created using the example data already in the files. The files are as follows:
invoice - each item in :invoices represents a single invoice you want to output in pdf
client - the list of clients (ensure ID match above)
addresses - your business address 
Enter your own invoice, client, business address data in resources and run as below.

## Run

1. Get leiningen (available through brew or https://leiningen.org/)
2. From command line:
```lein run```
    
PDFs will appear in project directory 
