(ns ident
  (:require [odoyle.rules :as o]))

(def order-ranks {:ident-without-responsible 4
                  :ident-with-responsible 3
                  :group-without-parent 2
                  :group-with-parent 1})

(def new-ident {:operation/operation :create
                :operation/target :ident
                :operation/data {:data/response-for ["xablauzinho-01"
                                                     "xablauzinho-02"]}})

(def new-ident-2 {:operation/operation :create
                  :operation/target :ident
                  :operation/data {:data/response-for []}})

(def ident-calculate-priority
  (o/->rule
   :rule/ident-calculate-priority
   [:what
    '[?id :operation/target ?target]
    '[?id :operation/data ?data]
    :when
    (fn [{:keys [?target]}]
      (= ?target :ident))
    :then
    (fn [{:keys [?id  ?data]}]
      (->> (o/insert
            o/*session*
            ?id
            {:ident.new/priority (if (> (-> ?data :data/response-for count) 1)
                                   (:ident-with-responsible order-ranks)
                                   (:ident-without-responsible order-ranks))})
           o/reset!))]))

(def get-priority
  (o/->rule
   :rule/get-priority
   [:what
    '[?id :ident.new/priority ?prio]]))

(defn -main
  "Invoke me with clojure -M -m scratch"
  [& _args]
  (let [session (-> (o/->session)
                    (o/add-rule ident-calculate-priority)
                    (o/add-rule get-priority)
                    (o/insert "xablau-01" new-ident)
                    (o/insert "xablau-02" new-ident-2)
                    o/fire-rules)]
    (-> session (o/query-all :rule/get-priority) println)))

