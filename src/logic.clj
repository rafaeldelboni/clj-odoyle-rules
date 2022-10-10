(ns logic)

(def order-ranks {:ident-without-responsible 4
                  :ident-with-responsible 3
                  :group-without-parent 2
                  :group-with-parent 1})

(def new-ident {:operation :create
                :target :ident
                :data {:response-for ["xablauzinho-01" "xablauzinho-02"]}})

(def new-ident-2 {:operation :create
                  :target :ident
                  :data {:response-for []}})

(def new-group {:operation :create
                :target :group
                :data {:parents ["banana" "maca"]}})

(def new-group-2 {:operation :create
                  :target :group
                  :data {:parents []}})

(defn priorization [{:keys [target data] :as op}]
  (case target
    :ident (if (> (-> data :response-for count) 1)
             (assoc op :prio (:ident-with-responsible order-ranks))
             (assoc op :prio (:ident-without-responsible order-ranks)))
    :group (if (> (-> data :parents count) 1)
             (assoc op :prio (:group-with-parent order-ranks))
             (assoc op :prio (:group-without-parent order-ranks)))))

(->> [new-ident new-ident-2 new-group new-group-2]
     (map priorization)
     (sort-by :prio #(> %1 %2)))
