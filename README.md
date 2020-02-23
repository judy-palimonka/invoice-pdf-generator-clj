# invoice-generator-clj
Generates invoices and business statements in PDF based on files provided in resources folder. 

## Usage
Look at files in resources to get the gist of data points. The files are as follows:
- addresses.edn - your business address
- clients.edn - your list of clients 
- invoices.edn - each item in :invoices represents a single invoice you want to output in PDF

NB. Ensure ID of address and customer provided in invoices file match those provided in addresses/clients.

## Run

1. Get leiningen (available through brew or https://leiningen.org/)
2. From command line:
```lein run```
    
This will create new folders 'invoices' and 'business-statements' where the PDFs will be written. 

## Planned Improvements
### Features
- Read from CSV
- Provide more PDF templates

### Code Refactor
- Flatten resource EDNs
- Move PDF mark-up out of clj file into an EDN config
- Add tests
- Allow for input arguments 

## Notes 
This was my first ad-hoc Clojure project written back in 2015 to help out a self-employed friend who was behind on paperwork. This was a quick and dirty thing so excuse messy and poorly structured code. Improvemnets coming soon
