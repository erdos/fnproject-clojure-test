(ns fnproject-clojure-test.core
  (:gen-class))

(set! *warn-on-reflection* true)

(def app-name (System/getenv "FN_APP_NAME"))

(def path (System/getenv "FN_PATH"))

(defn get-http-method
  "The HTTP method of the request"
  [] (some-> (System/getenv "FN_METHOD") str .toLowerCase keyword)  )

(def fn-type
  "the type for this call, currently 'sync' or 'async'"
  (System/getenv "FN_TYPE"))

(def fn-memory
  "a number representing the amount of memory available to the call, in MB"
  (System/getenv "FN_MEMORY"))

;; other headers:

;; FN_DEADLINE - RFC3339 time stamp of the expiration (deadline) date of function execution.
;; FN_REQUEST_URL - the full URL for the request (parsing example)
;; FN_CALL_ID - a unique ID for each function execution.
;; FN_METHOD - http method used to invoke this function
;; FN_HEADER_$X - the HTTP headers that were set for this request. Replace $X with the upper cased name of the header and replace dashes in the header with underscores.
;; $X - $X is the header that came in the http request that invoked this function.

(def fn-cpus
  "a string representing the amount of CPU available to the call, in MilliCPUs or floating-point number, eg. 100m or 0.1. Header is present only if cpus is set for the route."
  (System/getenv "FN_CPUS"))

(defn get-http-headers []
  (into {} (for [[^String k ^String v] (System/getenv)
                 :when (.toLowerCase (.startsWith k "FN_HEADER_"))]
             [(.substring k 10) v])))

(defn handler [request]
  {:body "OK"})


(defn read-request []
  (let [first-line   (vec (.split (str (read-line)) " "))

        method       (keyword (.toLowerCase (first first-line)))
        route        (second first-line)
        http-version (nth first-line 2)

        ;; read lines until empty
        headers (into {} (for [l (repeatedly read-line)
                               :while (not (clojure.string/blank? l))
                               :let [[k v] (vec (.split (str l) ": "))]]
                           [(.toLowerCase (str k)) (str v)]))
        ;; TODO: ring specs says sth about repeating headers!

        body ; read cl number of bytes from standard input.
        (when-let [cl (some-> (headers "content-length") Integer/parseInt)]
          (let [buf (byte-array cl)]
            (loop [read 0]
              (if (< read cl)
                (recur (+ read (.read *in* buf (int read) (int (- cl read)))))
                buf))))]
    {:method  method
     :path    route
     :headers headers
     :body    (str body)}
    ))

#_
(defn write-response [response-map]
  (let [out-cnt (count (:body response-map))
        headers (assoc (:headers response-map) "content-length" out-cnt)]
    ;; todo: statusz ellenorzes kell ide!
    (println "HTTP/1.1 200 OK")
    (doseq [[k v] (:headers response-map)]
      (println k ":" v))
    (println)
    (println body)))

(defn -main
  "Application entry point"
  [& args]
  (try (while true
         (let [request (read-request)
               body    (pr-str request)]
           (println "HTTP/1.1 200 OK")
           (println "Content-Length:" (count body))
           (println)
           (println body)
           ))
       (catch Throwable t (binding [*out* *err*] (println (pr-str t))))
       )
  )
