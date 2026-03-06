(ns navalgrid.persistence.data )

(def large-regular-squares
  [
   {:id ["ÄJ" "ÄH" "ÄG" "ÄF"]
    :nw [85.2 -76]
    :se [77.1 -35.5]}
   ])

(def large-partial-squares
  [
   {:id  ["OF" "OT"]
    :nw  [41.9 167]
    :se  [33.8 170.6]
    :sub [1 4 7]
    :o   :v}
   ])

(def irregular-squares
  [
   {:id ["ÄA"]
    :nw [60.9 -71.5]
    :se [59.1 -44.5]
    :o  :v}
   ])

(def polygonal-squares
  [
   {:id   ["AD"]
    :poly [[69 -37.75] [69 -24.25] [60.9 -24.25] [60.9 -37.3] [59.1 -37.3] [59.1 -44.5] [66.3 -44.5] [66.3 -37.75]]}
   ])

(def partial-squares
  [
   {:id ["AD1" "AD2"]
    :nw [69 -37.75]
    :se [66.3 -31]
    :o  :h}
   {:id  ["AL4" "AL5"]
    :nw  [56.4 -26.5]
    :se  [53.7 -23.5]
    :sub [1 2 4 5 7 8]
    :o   :h}
   ])

(def two-by-five-squares
  [
   {:id ["AK1" "AK2" "AK3" "AL1" "AL2" "AL3" "AM1" "AM2" "AM3" "AN1"]
    :nw [60.9 -37.3]
    :se [56.4 -33.7]
    :so :v
    :o  :h}
   ])
