(ns kafka-streams-example.kstream-kstream-outer-join-example
  (:require [kafka-streams-example.utils :as kstream-utils])
  (:import (org.apache.kafka.streams StreamsBuilder)
           (org.apache.kafka.streams.kstream KStream ValueJoiner JoinWindows)))

(defn impressions-clicks-topology
  [^KStream impressions ^KStream clicks]
  (-> impressions
      (.outerJoin clicks
                  (reify ValueJoiner
                    (apply [_ left right]
                      ((fn [impression-value click-value]
                         (str impression-value "/" click-value))
                       left right)))
                  (. JoinWindows of 5000))))

(defn builder-streaming-join-topology
  []
  (let [builder (StreamsBuilder.)
        ad-impressions-topic "adImpressions"
        ad-clicks-topic "adClicks"
        output-topic "output-topic"
        impressions (kstream-utils/build-stream builder ad-impressions-topic)
        clicks (kstream-utils/build-stream builder ad-clicks-topic)]

    (-> (impressions-clicks-topology impressions clicks)
        (.to output-topic))
    builder))
