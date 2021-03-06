 (ns clj-chess-sumarry.core
   (:gen-class)
   (:use [mikera.image.core])
   (:require
    [clojure.core.match :refer [match]]
    [clj-chess.game :as g]
    [clojure.java.io :as io]
    [clojure.string :as s]
    [djy.char :as c]
    [clj-chess.board :as b]))

(import '(java.awt.image AffineTransformOp)
        '(java.awt.geom AffineTransform)
        '(java.awt Graphics2D)
        '(javax.imageio ImageIO)
        '(java.lang StringBuilder)
        '(java.awt.image BufferedImage))

(def square-size "Size of board squares." 60)

(def img-files "File names for piece images."
  ["empty.png" "bb.png"   "bk.png"  "bq.png"  "wb.png"   "wk.png"  "wq.png"
   "bkn.png"  "bp.png"  "br.png"  "wkn.png"  "wp.png"  "wr.png"])

(def images "Map linking the keys for pieces to their loaded image files." 
  (zipmap [:empty :black-bishop :black-king :black-queen :white-bishop :white-king :white-queen
           :black-knight :black-pawn :black-rook :white-knight :white-pawn :white-rook] (map load-image-resource img-files)))

(defn letter-to-piece [l]
  "Returns the corresponding piece for letter l in a FEN."
  (match l
    \p :black-pawn
    \P :white-pawn
    \n :black-knight
    \N :white-knight
    \b :black-bishop
    \B :white-bishop
    \r :black-rook
    \R :white-rook
    \k :black-king
    \K :white-king
    \q :black-queen
    \Q :white-queen
    :else (throw (Exception. (format "Invalid FEN character %c." l)))))

(defn digit-to-piece [d]
  "Parses digits corresponding to empty squares in a FEN. Returns a sequence
  containing d :empty pieces."
  (repeat (Integer/parseInt (str d)) :empty))

(defn piece-to-img [images c]
  "Operator that adds the BufferedImages corresponding to the FEN piece symbol c
  to images."
  (if (c/letter? c)
    (conj images (letter-to-piece c))
    (into [] (concat images (digit-to-piece c)))))

(defn ranks [fen]
  "Splits FEN position field into ranks."
  (s/split fen #"/"))

(defn position-field [fen]
  "Returns the portion of the FEN that describes the ranks."
  (first (s/split fen #" ")))

(defn nth-fen-summary [n game]
  "Returns a sequence with the FENs of every nth move board state."
  (map b/to-fen (rest (take-nth (* 2 n) (g/boards game)))))

(defn draw-image [rank board-img file piece-img]
  "Draws the piece-img on the board-img at the rank and file position."
  (let [graphics (.getGraphics board-img)]
    (.drawImage graphics piece-img
                (AffineTransformOp.
                 (AffineTransform.) (AffineTransformOp/TYPE_BILINEAR))
                (* file square-size) (* rank square-size))
    board-img))

(defn draw-rank [board rank pieces]
  "Draws the files along the rank on the board."
  (reduce-kv (partial draw-image rank) board (into [] (map images (reduce piece-to-img [] pieces)))))

(defn draw-fen [fen]
  "Returns a BufferedImage for FEN."
  (let [board-template (load-image-resource "board.png")]
    (last (map-indexed (partial draw-rank board-template) (->> fen
                                                               position-field
                                                               ranks)))))

(defn draw-nth-fen [n game]
  "Returns BufferedImages of every nth board FEN."
  (map draw-fen (nth-fen-summary n game)))

(defn n-moves [game]
  "Returns the number of 2-moves from 1-moves."
  (Math/round (float (/ (count (g/moves game)) 2))))

(defn save-image [path img]
  "Writes img to path."
  (. ImageIO write img "png" (io/file path)))

(defn filenames [base n]
  "Generates filenames for all the board images."
  (map #(str base "-" % ".png") (range 0 n)))

(defn write-images
  ([n game out-file]
   "Writes the image of every nth move board to numbered files with out-file as basename."
   (let* [drawn-fens (draw-nth-fen n game)
          n-boards (count drawn-fens)
          files (filenames out-file n-boards)]
     (doall (map save-image files drawn-fens))
     (map vector (range 1 (inc (n-moves game)) n) files))))

(defn move-interval-text [moves i j]
  "Returns the interval of moves [i, j) from the pgn."
  (s/join " " (map #(str %1 "." %2) (range i j) (take j (drop (dec i) moves)))))

(defn moves-list-text [game]
  "Returns a list of strings containing the ordered non-numbered moves."
  (rest (map s/trim (s/split (g/move-text game) #"\d*\."))))

(defn write-html [game image-files html-file]
  "Writes a html-file containing the pgn text interspersed with image-files."
  (let [move-list (doall (moves-list-text game))
        html (StringBuilder.)]
    (.append html "<html><body>")
    (reduce (fn [i1 i2]
              (.append html
                       (str "</br><code>"
                            (move-interval-text move-list
                                           (first i1)
                                           (first i2))
                            "</code></br>"))
              (when (second i1)
                (.append html (format "<img src=%s>" (second i1))))
              i2)
             image-files)
    (.append html "</body></html>")
    (spit html-file (.toString html))))

(defn -main [interval in-file out-file]
  (let [game (first (g/games-in-file in-file))
        image-files (write-images (Integer/parseInt interval) game out-file)]
    (write-html game image-files out-file)))
