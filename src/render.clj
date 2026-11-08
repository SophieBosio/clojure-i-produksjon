(ns render
  (:require
   [style]
   [clojure.string :as str]
   [hiccup2.core :as hiccup]))

(def status->label
  {:reading "📖 Reading"
   :finished "✅ Finished"
   :want-to-read "🔖 Want to Read"})

(defn ->star-rating
  [rating]
  (if (pos-int? rating)
    (apply str (concat (repeat rating "⭐") (repeat (- 5 rating) "  ·  ")))
    (apply str (repeat 5 "  ·  "))))

(defn ->publication-stamp
  [publication-year]
  (if publication-year
    (str "📅 " publication-year)
    "N/A"))

(defn ->authors-line
  [authors]
  (case (count authors)
    0 nil
    1 (first authors)
    2 (str (first authors) " & " (second authors))
    (str (str/join ", " (butlast authors)) " & " (last authors))))

(defn book-card [book]
  [:a {:href (str "/books/" (:id book))
       :style style/invisible-link}
   [:article {:style style/card}
    [:h3 {:style style/book-title} (:title book)]
    [:p {:style style/book-author} (->authors-line (:authors book))]
    [:div {:style style/book-info}
     [:span {:style style/pill} (->publication-stamp (:publication-year book))]
     [:span {:style style/pill} (->star-rating (:rating book))]]]])

(defn section
  [status books]
  [:section {:style style/section-header}
   [:h2 {:style style/section-divider}
    (status->label status)]
   (map book-card books)])

(defn page-header
  [title]
  [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:link {:rel "stylesheet" :href style/font-url}]
    [:title title]
    [:style (hiccup/raw style/page)]])

(defn frontpage
  [books]
  [:html
   (page-header "My Bookshelf")
   [:body {:style style/wide-page}
    [:header {:style style/page-header}
     [:h1 {:style style/page-title} "My Bookshelf"]
     [:p {:style style/subtitle} (str (count books) " books and counting")]]
    [:main {:style style/columns}
     (let [grouped (group-by :status books)
           order [:want-to-read :reading :finished]]
       (for [status order
             :let [section-books (get grouped status)]
             :when (seq section-books)]
         (section status section-books)))]]])

(defn book-detail
  [book]
  [:html
   (page-header (:title book))
   [:body
    [:nav
     [:a {:href "/" :style style/back-link} "← Back to bookshelf"]]
    [:main
     [:article {:style style/detail}
      (when (:cover-image book)
        [:img {:src (str "/images/" (:cover-image book))
               :alt (:title book)
               :style style/detail-cover}])
      [:h1 {:style style/detail-title} (:title book)]
      [:p {:style style/book-author} (->authors-line (:authors book))]
      [:div {:style style/detail-info}
       [:span {:style style/pill} (->publication-stamp (:publication-year book))]
       [:span {:style style/pill} (->star-rating (:rating book))]
       [:span {:style style/pill} (status->label (:status book))]]]]]])

(defn not-found
  []
  [:html
   (page-header "Not Found")
   [:body
    [:main {:style style/message}
     [:h1 {:style style/page-title} "📖 404"]
     [:p {:style style/subtitle} "That book doesn't seem to be on the shelf."]
     [:nav {:style "margin-top: 24px;"}
      [:a {:href "/" :style style/back-link} "← Back to bookshelf"]]]]])

(defn malformed
  []
  [:html
   (page-header "Invalid")
   [:body
    [:main {:style style/message}
     [:h1 {:style style/page-title} "❌ 422"]
     [:p {:style style/subtitle} "Not a valid book ID :("]
     [:nav {:style "margin-top: 24px;"}
      [:a {:href "/" :style style/back-link} "← Back to bookshelf"]]]]])
