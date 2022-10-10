(ns scratch
  "FIXME: my new org.corfield.new/scratch project."
  (:require [odoyle.rules :as o]))

(def order-ranks [{:order :ident-without-responsible
                   :priority 4}
                  {:order :ident-with-responsible
                   :priority 3}
                  {:order :group-without-parent
                   :priority 2}
                  {:order :group-with-parent
                   :priority 1}])

(def same-name-rule
  (o/->rule
   :ident/same-name-rule
   [:what
    '[?id :ident.new/nome ?new]
    '[?id :ident.old/nome ?old]
    :when
    (fn [{:keys [?new ?old]}]
      (= ?new ?old))
    :then
    (fn [{:keys [?id] :as match}]
      (println :ident/same-name-rule "save!" match)
      (->> (o/insert o/*session* ?id {:ident/with-same-name :banana})
           o/reset!))
    :then-finally
    (fn []
      (println "Acabou"))]))

(def one-group-rule
  (o/->rule
   :ident/one-group-rule
   [:what
    '[?id :ident.new/groups ?new]
    '[?id :ident.old/groups ?old]
    '[?id :ident/with-same-name ?same-name]
    :when
    (fn [{:keys [?new]}]
      (= (count ?new) 1))
    :then
    (fn [match]
      (println :ident/one-group-rule "save!" match))
    :then-finally
    (fn []
      (println "Acabou"))]))

(defn -main
  "Invoke me with clojure -M -m scratch"
  [& _args]
  (let [session (-> (o/->session)
                    (o/add-rule same-name-rule)
                    (o/add-rule one-group-rule)
                    (o/insert 1 (merge #:ident.new{:nome "lula"
                                                   :cargo "Presidente"
                                                   :groups [{:nome "palacio" :type :org}
                                                            {:nome "sao-bernardo" :type :org}]}
                                       #:ident.old{:nome "lula"
                                                   :cargo "Ex-Presidente"
                                                   :groups [{:nome "sao-bernardo" :type :org}]}))

                    (o/insert 2 (merge #:ident.new{:cargo "Ex-Presidente"}
                                       #:ident.old{:nome "bozo" :cargo "Presidente"}))

                    (o/insert 3 (merge #:ident.new{:nome "ozob" :cargo "Ex-Presidente"}
                                       #:ident.old{:nome "bozo" :cargo "Presidente"}))

                    (o/insert 4 (merge #:ident.new{:nome "bozo"
                                                   :cargo "Ex-Presidente"
                                                   :groups [{:nome "presidio" :type :org}]}
                                       #:ident.old{:nome "bozo"
                                                   :cargo "Presidente"
                                                   :groups [{:nome "palacio" :type :org}]}))

                    (o/insert 5 (merge #:ident.new{:nome "bozo"
                                                   :cargo "Ex-Presidente"
                                                   :groups [{:nome "presidio" :type :org}]}
                                       #:ident.old{:nome "ozob"
                                                   :cargo "Presidente"
                                                   :groups [{:nome "palacio" :type :org}]}))
                    o/fire-rules)]
    (-> session (o/query-all :ident/same-name-rule) println)
    (-> session (o/query-all :ident/one-group-rule) println)))
