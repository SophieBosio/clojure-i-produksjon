(ns style
  (:require
   [clojure.string :as str]))

(def clojure-colors
  {:dark-blue "#5881d8"
   :light-blue "#90b4fe"
   :dark-green "#63b132"
   :light-green "#91dc47"})

(defn blob
  [color position]
  (str "radial-gradient(circle at " position ", "
       (clojure-colors color) "55, transparent 60%)"))

(def blurred-background
  (str "background-color: #f8f8f8; "
       "background-image: "
       (str/join ", " [(blob :dark-blue "0% 0%")
                       (blob :light-blue "100% 0%")
                       (blob :dark-green "100% 100%")
                       (blob :light-green "0% 100%")])
       "; "
       "background-attachment: fixed; "))

(def font-url
  ;; Fraunces font:
  "https://fonts.googleapis.com/css2?family=Fraunces:opsz,wght@9..144,100..900&display=swap")

(def text-muted
  "color: #5b6472;")

(def glass
  (str "background: rgba(255, 255, 255, 0.65); "
       "backdrop-filter: blur(12px); "
       "-webkit-backdrop-filter: blur(12px); "
       "border: 1px solid rgba(255, 255, 255, 0.7); "
       "border-radius: 16px; "
       "box-shadow: 0 4px 24px rgba(30, 41, 59, 0.08);"))

(def page
  (str "*, *::before, *::after { box-sizing: border-box; } "
       "body { "
       "font-family: 'Fraunces', Georgia, serif; "
       "max-width: 720px; "
       "min-height: 100vh; "
       "margin: 0 auto; "
       "padding: 56px 20px; "
       "line-height: 1.5; "
       "-webkit-font-smoothing: antialiased; "
       blurred-background
       "color: #1e293b; "
       "} "
       "h1, h2, h3 { letter-spacing: -0.02em; } "
       "a > article { transition: transform 150ms ease, box-shadow 150ms ease; } "
       "a:hover > article { transform: translateY(-2px); "
       "box-shadow: 0 10px 32px rgba(30, 41, 59, 0.14) !important; } "
       "nav a:hover { color: " (clojure-colors :dark-blue) " !important; } "
       "form button:hover { background: " (clojure-colors :dark-green) " !important; } "
       "form select:focus, form button:focus-visible { outline: 2px solid "
       (clojure-colors :light-blue) "; outline-offset: 2px; }"))

(def page-header
  "text-align: center; margin-bottom: 48px;")

(def page-title
  (str "font-size: 2.75rem; "
       "font-weight: 800; "
       "margin: 0 0 8px 0;"))

(def subtitle
  (str text-muted
       " margin: 0;"))

(def card
  (str glass
       " padding: 20px 24px; "
       "margin: 0 0 14px 0;"))

(def book-title
  (str "font-size: 1.15rem; "
       "font-weight: 650; "
       "margin: 0 0 2px 0;"))

(def book-author
  (str text-muted
       " margin: 0 0 12px 0;"))

(def book-info
  (str "display: flex; "
       "flex-wrap: wrap; "
       "gap: 8px; "
       "font-size: 13px;"))

(def pill
  (str "display: inline-block; "
       "padding: 3px 10px; "
       "border-radius: 999px; "
       "background: rgba(255, 255, 255, 0.8); "
       "border: 1px solid rgba(30, 41, 59, 0.08); "
       text-muted))

(def wide-page
  "max-width: 1200px;")

(def stack-below "900px")

(def columns
  (str "display: flex; "
       "flex-wrap: wrap; "
       "gap: 40px 24px; "
       "align-items: flex-start;"))

(def section-header
  (str "flex: 1 1 calc((" stack-below " - 100%) * 999); "
       "min-width: 0;"))

(def section-divider
  (str "font-size: 1rem; " 
       "text-transform: uppercase; "
       "letter-spacing: 0.08em; "
       text-muted
       "font-weight: 800; "
       " margin: 0 0 14px 4px;"))

(def invisible-link
  "display: block; text-decoration: none; color: inherit;")

(def detail
  (str glass
       " text-align: center; "
       "padding: 40px 32px; "
       "margin-top: 24px;"))

(def detail-cover
  (str "max-width: 260px; "
       "width: 100%; "
       "border-radius: 10px; "
       "box-shadow: 0 12px 40px rgba(30, 41, 59, 0.25); "
       "margin: 0 0 28px 0;"))

(def detail-title
  (str "font-size: 2rem; "
       "font-weight: 800; "
       "margin: 0 0 4px 0;"))

(def detail-info
  (str book-info
       " justify-content: center;"))

(def back-link
  (str text-muted
       " font-size: 14px; "
       "font-weight: 500; "
       "text-decoration: none; "
       "transition: color 150ms ease;"))

(def message
  (str glass
       " text-align: center; "
       "padding: 48px 32px;"))

(def rating-form
  (str "display: flex; "
       "flex-wrap: wrap; "
       "align-items: center; "
       "justify-content: center; "
       "gap: 10px; "
       "margin-top: 28px; "
       "padding-top: 24px; "
       "border-top: 1px solid rgba(30, 41, 59, 0.08);"))

(def rating-label
  (str text-muted
       " font-size: 14px; "
       "font-weight: 500;"))

(def rating-select
  (str "font-family: inherit; "
       "font-size: 13px; "
       "padding: 4px 10px; "
       "border-radius: 999px; "
       "background: rgba(255, 255, 255, 0.8); "
       "border: 1px solid rgba(30, 41, 59, 0.08); "
       "color: #1e293b;"))

(def rating-button
  (str "font-family: inherit; "
       "font-size: 13px; "
       "font-weight: 600; "
       "padding: 4px 14px; "
       "border-radius: 999px; "
       "border: none; "
       "cursor: pointer; "
       "color: white; "
       "background: " (clojure-colors :dark-blue) "; "
       "transition: background 150ms ease;"))
