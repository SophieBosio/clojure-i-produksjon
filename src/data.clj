(ns data)

(random-uuid)

(def books
  [{:id "ec78654a-91ea-446f-900c-4957b359882a"
    :title "Structure and Interpretation of Computer Programs"
    :author-ids ["e8b1089b-7e49-4c14-9905-3beddc7de07f"
                 "99415308-f44b-452a-8489-ebeec9e52b39"
                 "fa794f5c-8394-4a9b-9170-9bcf3f75c0f5"]
    :publication-year 1985
    :rating 5
    :cover-image-id "4435b85c-2a27-4bd3-8baa-9759e5081af4"
    :status :reading}
   {:id "6b3476fd-c633-4d6a-8313-13be1c8a59fa"
    :title "Software Design for Flexibility"
    :author-ids ["4a55c0e5-e088-4ab5-92db-298c776dbf8c"
                 "99415308-f44b-452a-8489-ebeec9e52b39"]
    :publication-year 2021
    :cover-image-id "9ec8f9f2-c580-4b64-a60a-391db642fede"
    :status :want-to-read}
   {:id "586173d4-6b32-43cd-b75c-b051b62fc868"
    :title "The Little Schemer"
    :author-ids ["f223bb03-fc5b-4891-b4dd-f7058f5d4859"
                 "959429b3-8f2f-4abe-8026-432f2614f1e5"]
    :publication-year 1974
    :cover-image-id "fca8c5f5-a256-403d-859a-7e4d88a94ce3"
    :status :want-to-read}
   {:id "13dddc4c-add5-488d-857a-5d2df463c61c"
    :title "The Little LISPer"
    :author-ids ["f223bb03-fc5b-4891-b4dd-f7058f5d4859"
                 "959429b3-8f2f-4abe-8026-432f2614f1e5"]
    :publication-year 1989
    :cover-image-id "3f887e63-99af-4d6a-a814-4e20b7548afe"
    :status :want-to-read}
   {:id "91662646-7665-4a99-b3ce-66ff12a8b39d"
    :title "Elements of Clojure"
    :author-ids ["52b6e9c8-42ed-40ae-90eb-f2203e89c873"]
    :rating 5
    :publication-year 2019
    :cover-image-id "03ac012e-8167-4f59-aab0-72b9fe407e10"
    :status :finished}])

(def authors
  [{:id "e8b1089b-7e49-4c14-9905-3beddc7de07f"
    :name "Hal Abelson"}
   {:id "99415308-f44b-452a-8489-ebeec9e52b39"
    :name "Gerald Jay Sussman"}
   {:id "fa794f5c-8394-4a9b-9170-9bcf3f75c0f5"
    :name "Julie Sussman"}
   {:id "f223bb03-fc5b-4891-b4dd-f7058f5d4859"
    :name "Daniel Paul Friedman"}
   {:id "959429b3-8f2f-4abe-8026-432f2614f1e5"
    :name "Matthias Felleisen"}
   {:id "52b6e9c8-42ed-40ae-90eb-f2203e89c873"
    :name "Zachary Tellman"}
   {:id "4a55c0e5-e088-4ab5-92db-298c776dbf8c"
    :name "Chris Hanson"}])

(def cover-images
  [{:id "4435b85c-2a27-4bd3-8baa-9759e5081af4"
    :filename "sicp-cover.jpg"}
   {:id "9ec8f9f2-c580-4b64-a60a-391db642fede"
    :filename "software-design-for-flexibility-cover.jpg"}
   {:id "fca8c5f5-a256-403d-859a-7e4d88a94ce3"
    :filename "the-little-schemer-cover.jpg"}
   {:id "3f887e63-99af-4d6a-a814-4e20b7548afe"
    :filename "the-little-lisper-cover.jpg"}
   {:id "03ac012e-8167-4f59-aab0-72b9fe407e10"
    :filename "elements-of-clojure-cover.png"}])
