(ns invoice-pdf-generator-clj.core
  (:require [clj-pdf.core :as pdf])
  (:use [clojure.java.shell :only [sh]])
  (:gen-class))



(defn read-file [path]
  (read-string (slurp path)))

(def stylesheet
  {:foo {:color [255 0 0]
         :family :helvetica}
   :bar {:color [0 0 255]
         :family :helvetica}
   :text {:align :center :keep-together true :leading 1 :size 12}
   :text2 {:align :left }
   :medium {:align :center :keep-together true :leading 1.5 :style "bold" :size 14}
   :large {:align :center :keep-together true :leading 1.5 :style "bold" :size 16}
   :space-style-small {:leading 11 :align :center }
   :right {:leading 11 :align :right :size 12 }
   :left { :align :left :size 12 }
   :bs-cell {:align :left, :valign :middle, :size 6, :style "normal"}
   :bs-header {:align :left :valign :middle :style "bold" :size 7}
   :bs-small-header {:align :left :valign :middle :style "bold" :size 6}

   })

(defn get-from-external-file [id file]
  (let [result (->> (slurp file)
                 read-string
                 (filter #(= (:id %) id))
                 first)]
    (if (some? (seq result))
      result
      (throw (Exception. (str id " - not found"))))))

(defn get-customer [id]
  (get-from-external-file id "resources/clients.edn" ))


(defn get-address [id]
  (get-from-external-file id "resources/addresses.edn" ))

(defn get-pdf-invoice-template
  [address-id
   customer-id
   invoice-date-from
   invoice-date-to
   invoice-no
   invoice-amount
   payment-method]
  (let [
  					 address (get-address address-id)
        customer (get-customer customer-id)]
  [
   {:stylesheet stylesheet :footer false}
   [:spacer 2]
   [:paragraph.medium "BUSINESS_NAME"]
   [:spacer]
   [:paragraph.text "BUSINESS_DESCRIPTION"]
   [:spacer]
   [:paragraph.text (:address-line-1 address)]
   [:spacer]
   [:paragraph.text (:address-line-2 address)]
   [:spacer]
   [:paragraph.text "London"]
   [:spacer]
   [:paragraph.text (:postcode address)]
   [:spacer]
   [:paragraph.text "Phone: PHONE"]
   [:spacer]
   [:paragraph.text "Email: EMAIL"]
   [:spacer]
   [:line]
   [:spacer 2]
   [:paragraph.right (str "Issue Date: " invoice-date-to)]
   [:spacer 5]
   [:paragraph.large (str "Invoice No: " invoice-no)]
   [:spacer 2]
   [:table {:header [{:style :normal :border false } "Customer:" ]   :width 50 :border false :border-width 0.5 :cell-border false :align :center :size 14}
    [
     [:cell.space-style-small
      [:spacer ]
      [:paragraph.text (:name customer)]
      [:spacer ]
      [:paragraph.text (:address-line-1 customer)]
      [:spacer ]
      [:paragraph.text (:address-line-2 customer)]
      [:spacer ]
      [:paragraph.text (:phone customer)]]]]

   [:spacer 2]
   [:pdf-table {:horizontal-align :center}
    [140 40 ]
    [[:pdf-cell {:align :center :valign :middle :style "bold" :min-height 40 :size 14} "DESCRIPTION" ]
     [:pdf-cell {:align :center :valign :middle :style "bold" :min-height 40 :size 14} "AMOUNT"]  ]
    [[:pdf-cell {:align :left :valign :middle :min-height 40 :size 12}
      (str "BUSINESS_DESCRIPTION - period from " invoice-date-from " to "  invoice-date-to)
      [:spacer ]]
     [:pdf-cell {:valign :middle :align :right :size 12} "£ 120.00"
      [:spacer ]]
     ]]
   [:spacer 4]
   [:paragraph.left "                 Total:                     £" invoice-amount]
   [:spacer]
   [:paragraph.left "                 Balance Due:        £" invoice-amount]
   [:spacer]
   [:paragraph.left "                 Payment Method:   " payment-method]]))


(def business-statement-table-pdf-header
  [[:pdf-cell.bs-header "Billing Month"]
   [:pdf-cell.bs-header "Invoice Issue Date"]
   [:pdf-cell.bs-header "Invoice Number"]
   [:pdf-cell.bs-header "Client"]
   [:pdf-cell.bs-header "Billed Ammount"]
   [:pdf-cell.bs-header "Monthly Amount"]

   ])

(defn get-first-monthly-record [first-in-month-record monthly-record-count monthly-sum]
  [[:pdf-cell {:align :left :valign :middle :style "normal" :size 6 :rowspan monthly-record-count}
    (clojure.string/replace (re-find #"\/[0-9][0-9]\/" (first first-in-month-record)) "/" "")]
   [:pdf-cell.bs-cell (first first-in-month-record)]
   [:pdf-cell.bs-cell (second first-in-month-record)]
   [:pdf-cell.bs-cell (nth first-in-month-record 2)]
   [:pdf-cell.bs-cell (str (nth first-in-month-record 3))]
   [:pdf-cell {:align :left :valign :middle :style "normal" :size 6 :rowspan monthly-record-count} (str monthly-sum)]
   ]
  )

(defn get-subsequent-monthly-record [record]
  [[:pdf-cell.bs-cell (first record)]
   [:pdf-cell.bs-cell (second record)]
   [:pdf-cell.bs-cell (nth record 2)]
   [:pdf-cell.bs-cell (str (nth record 3))]
   ])


(defn get-all-monthly-rows [yearly-invoice-data]
  (let [monthly-results
        (mapv (fn [distinct-month]
          (let [records                     (filterv #(= (first %) distinct-month) yearly-invoice-data)
                first-in-month-record       (first records)
                rest-in-month               (rest records)
                monthly-record-count        (count records)
                monthly-sum                 (apply + (map #(nth % 3) records))
                first-month-pdf-row         (get-first-monthly-record
                                              first-in-month-record
                                              monthly-record-count
                                              monthly-sum)
                all-monthly-records-pdf     (into []
                                              (reduce
                                                (fn [final current-record]
                                                  (conj final (get-subsequent-monthly-record current-record)))
                                                [first-month-pdf-row]
                                                rest-in-month))

                ]
            all-monthly-records-pdf
            ))
    (distinct (map first yearly-invoice-data)))
        ]
    (reduce concat [] monthly-results)
    ))



(defn get-pdf-structure-business-statement [year]
  (let [yearly-data      (get-in (read-file "resources/invoices.edn") [(keyword (str year)) :invoices])

        monthly-pdf-rows (get-all-monthly-rows yearly-data)
        a (print monthly-pdf-rows)
        ]

    [{:stylesheet stylesheet :footer false}
     (reduce
       conj
       [:pdf-table
        {:horizontal-align :center}
        [30 30 30 70 30 30]]
       (conj monthly-pdf-rows business-statement-table-pdf-header))]))


(defn get-business-statement [year]
  (sh "mkdir" "-p" (str "business-statements" ))

  (pdf/pdf
    (get-pdf-structure-business-statement year )
    (str "business-statements/" year ".pdf")
    ))


(defn generate-pdf-invoice-from-template
  [address-id
   customer-id
   invoice-date-from
   invoice-date-to
   invoice-no
   invoice-amount
   payment-method]
   (sh "mkdir" "-p" (str "invoices/"  customer-id))
  (pdf/pdf
    (get-pdf-invoice-template
      address-id
      customer-id
      invoice-date-from
      invoice-date-to
      invoice-no
      invoice-amount
      payment-method)
    (str "invoices/" customer-id "/" invoice-no  ".pdf")))

(defn -main
  "Source data from resources and print out PDFs"
  [& args]
  (do
  			(sh "mkdir" "-p" "invoices")
  			(doseq [ [ year invoices ] (read-file "resources/invoices.edn") 
  							invoice (:invoices invoices)]
  					(apply generate-pdf-invoice-from-template invoice ))

  			(doseq [i (read-file "resources/invoices.edn") ]
  						(get-business-statement (first i)))
  ))

