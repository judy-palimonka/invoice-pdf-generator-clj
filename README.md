# invoice-generator-clj
Generates invoices and business statements in PDF based on data from resources. 

## Usage
Look at files in resources to get the gist of data points that are accepted. The files are as follows:
- addresses.edn - your business address
- clients.edn - your list of clients 
- invoices.edn - each item in :invoices represents a single invoice you want to output in PDF (ensure ID of address and customers match IDs in above files)

## Run

1. Get leiningen (available through brew or https://leiningen.org/)
2. From command line:
```lein run```
    
This will create new folders 'invoices' and 'business-statements' where the PDFs will be written. 

## Notes 
This wasw my first ad-hoc Clojure project written back in 2015 to help out a friend who was behind on paperwork and needed to provide a summary of business records to clients / accountant. This was a quick and dirty thing so excuse messy and poorly structured code.  Will improve soon. 

### Planned Improvements
### Features
- Read from CSV
- Provide various PDF templates

### Code Refactor
- Flatten resource EDNs
- Move PDF mark-up out of clj file into an EDN config
- Add tests
- Allow for input arguments 
