(ns se.citerus.dddclojure.domain.order)

;;Order aggregate

(defprotocol Order
  (add-item [this product qty piece-price] "Add an item to the order")
  (remove-item [this product qty] "Remove item from order")
  (order-total [this] "Calculate order total")
  (close-order [this] "Close order, it is not cool to add or remove items to a closd order"))

(defn line-total [{:keys [qty piece-price]}]
  (* qty piece-price))

(defn find-line-item [lines product]
  (filter #(= (:product %) product) lines))

(defrecord LineItem [product qty piece-price])

(defrecord SimpleOrder [number date status limit lines] ;Create FastOrder where LineItems are in a map
  Order
  (add-item [this product qty piece-price]
    (update-in this [:lines] assoc product (LineItem. product qty piece-price)))
  (order-total [this]
    (reduce #(+ %1 (line-total %2)) 0 (vals (:lines this))))
  (remove-item [this product qty]
    (update-in this [:lines product :qty] - qty))
  )


;; Order factory methods

(defn create-order [number date limit]
  ;User :pre condition to handle limit
  (SimpleOrder. number date ::open limit {}))


