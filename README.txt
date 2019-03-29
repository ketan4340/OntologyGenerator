***********************************
***** OntologyGeneratorの使い方 *****
***********************************
2019.3.29
田邉憲太朗 ( t1311128@mail.uec.jp )

**** OntologyGeneratorとは ****
日本語の文章をその文意を表すRDFデータに変換します．


**** 実行の仕方 ****
*** 入力テキストファイルについて
入力テキストファイルは「一文毎に改行されている」必要があります．
また，文頭に主語が含まれることを想定しているので「～は」で始まっていないと何も出力されないでしょう．
文字コードは「UTF-8」推奨です．
OntologyGenerator\resource\input の下にいくつかテキストファイルが入っています．
特に， OntologyGenerator\resource\input\goo\text\gooText生物-動物名-All.txt は実験にも使った，約16,000文の国語辞書の語義文が入っています．(goo国語辞書からクローリングしたもので，~input/goo/dicには，余分な括弧などを取り除く前のテキストが[見出し語TAB語義文]の形のまま残っています)

*** jarファイルから実行
OntologyGeneratorディレクトリ上で実行しないと指定のパスがないというエラーが出ると思います．ご容赦ください．
* WIndows
 % java -var -Dfile.encoding=UTF-8 実行可能jarファイル 入力テキストファイル
* Mac
 % java -var 実行可能jarファイル 入力テキストファイル
実行可能ファイルは
  workspace\OntologyGenerator\dist\OntologyGenerator-1.5.8.jar
が最新のものです．

*** Eclipse等IDEから実行
OntologyGeneratorプロジェクトの構成ビルドパスに，
プロジェクトに CabochaExecutor
ライブラリに apache-jena のjarファイルを指定してください．
CabochaExecutorはOntologyGeneratorと同じworkspace内に，jarライブラリは workspace\lib\apache-jena-3.8.0 に入っています．

OntologyGenerator\src\main\Generator.java にメインメソッドがあります．
同ファイルのexecuteメソッドに手軽に入力テキストやコンソールへの出力を切り替えられるようにコメントアウトされたコードがあり，コードを書き換えつつ繰り返し実行するときに便利です．



**** カスタマイズ ****

*** プロパティファイル ***
実行に必要な入出力のファイル (オントロジーにしたいテキストファイルではない) を指定できます．
パス
 % OntologyGenerator\src\conf\property.xml

** プロパティのキー **
 "noun-file": 入力テキストに含まれることがわかっている名詞を書いたファイル．結局CabochaExecutor側で処理してしまったので使っていない(CabochaExecutor\dest\usrdic\user.dicがそれを担っています)．
 "date_words-file": 日付，季節など時間に関する単語を書いたファイル．Cabochaが場所や時間に関する単語にタグ付けをしてくれるのだが，たしか「夏季」などは時間とは認識してくれない．個人的にしてほしかったのでここに書くと補完されるようにした．
 "adjectival_regexes-file": 「形容詞っぽい名詞」に相当する条件を正規表現で書いたファイル．例えば「～色」を条件にしたければ「.+色」と書く．
 "extension_rule-file": 文構造RDFグラフを拡張する「拡張RDFルール」を書いたファイル．．例えば，CaboChaの解析では主語や述語の判定は行わないため，ここで「助詞が"は"なら主語なのでjass:subjectを追加」といった具合で文構造RDFグラフを拡張する．RDFルールについては後述．
   デフォルトパス: OntologyGenerator\resource\rule\expansionRules.txt
 "ontology_rules-dir": 「文構造RDFグラフからオントロジー(文意を表したRDF)へ」変換するRDFルール．IF部には文構造RDFグラフとマッチングを行うRDFグラフパターン(変数を含むRDFトリプル群)を書き，THEN部にはIF部がマッチした時の出力を書きます．
　　デフォルトパス: OntologyGenerator\resource\rule\ontology-rules\ 以下の3ファイル
 "default-JASS-file": 私が定義したJASS語彙の定義域，値域，クラスの階層などが書いてありますが，重要なのは冒頭の名前空間接頭辞(rdfs:subClassOfでいうところのrdfsのこと)の指定です．ここに接頭辞を登録しておけば，実行時にその接頭辞を使用して表示・出力されるので見やすいです．
 "use-entity_linking": trueにすると名詞リソースをDBPediaのリソースとリンクさせられたのですが，途中で仕様変更した影響で正しく動きません．falseのままでいいです．
 "sparql-endpoint": 上のリンク機能でアクセスするSPARQLエンドポイントの指定です．上記の通り機能自体が使えないので気にしないで．
 "max-size-of-INstatement": リンク機能で一度にいくつの名詞を書き連ねてクエリを投げるかの指定．これも気にしないで．
 "cabocha-prop": CabochaExecutorのプロパティファイルです．

 "output-shortsentence": 長文分割処理によって分割したテキストのログのパスです．
 "output-convertedJASS-turtle": 文構造RDFグラフのログのパス．
 "output-usedrules": 使用したRDFルールのログのパス．

 "output-ontology-turtle": 生成したオントロジーをTURTLE構文で出力するファイルのパス．
 "output-id_triple": 生成したオントロジーを評価しやすいようにルール番号と一緒に表記したCSVファイルのパス．
 


********
% src\grammar\Word\Phrase.java
は魔窟です．前者は名詞句のRDF化を担っているのですが，満足に完成することないまま終わりを迎えました．すみません．

わからないことだらけだと思いますが，聞きたいことがあれば上記のアドレスにメールをください．
