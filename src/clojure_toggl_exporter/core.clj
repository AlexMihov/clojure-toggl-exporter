(ns clojure-toggl-exporter.core
  (:refer-clojure :exclude [load])
  (:require [yaml.core :as yaml]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io])
  (:gen-class))

(defn extractTagsFromDescription
  "Extracts Tags which are embeded into the description of the time entry"
  [entry]
  (into #{} (re-seq #"#[-0-9a-zA-Z]*" (:description entry))))

(defn stripTagsFromDescription
  "Takes an entry, looks up the description and removes #tags from it"
  [entry]
  (last (re-find #"(#[-0-9a-zA-Z]* )+(.*)" (:description entry))))

(defn calculateDecimalHours
  "Takes a duration in miliseconds and returns a float representation in hours"
  [duration]
  (format "%.12f" (float (/ duration (* 60 60)))))

(defn hashtagerize
  "Adds a pound sign / hashtag in front of the input"
  [input]
  (str "#" input))

(defn write-csv [path row-data]
  (let [columns (into [] (keys (first row-data)))
        headers (map name columns)
        rows (mapv #(mapv % columns) row-data)]
    (with-open [file (io/writer path)]
      (csv/write-csv file (cons headers rows)))))

(defn manipulateEntry
  "Extracts and prepares the entries for export"
  [entry]
  ;; prepare properties for export map
  (def extractedHashtags (extractTagsFromDescription entry))
  (def tags (map hashtagerize (:tags entry)))
  
  (def formatter (f/formatters :year-month-day))
  (def date (f/unparse formatter (f/parse (:start entry))))
  (def duration (calculateDecimalHours (:duration entry)))
  (def hashtags (clojure.string/join " " (concat tags extractedHashtags)))
  (def description (stripTagsFromDescription entry))

  ;; construct export entry
  {:date date :duration duration :hashtags hashtags :description description})

(defn getEntries
  "Gets entries from the API using parameters since and until"
  []
  (json/read-str (:body (client/get
                         "https://toggl.com/api/v8/time_entries?start_date=2019-04-11T10%3A42%3A46%2B02%3A00&end_date=2019-04-19T15%3A42%3A46%2B02%3A00&pid=148987219"
                         {:basic-auth "869faf8fbbcab6fc278b83fadb8b2b4b:api_token"})) :key-fn keyword))

(defn -main
  "Connects to toggl and using the config file extracts and formats the entries"
  [& args]
  (def config (yaml/from-file "config.yml"))
  (def timeEntries (getEntries))
  (def cleanedUpEntries (map manipulateEntry timeEntries))
  (write-csv "/Users/alex/Documents/200ok/team/customers/li/swisscom_tv/timetracking/results.csv"
             cleanedUpEntries))
