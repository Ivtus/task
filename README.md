# task
Evening Music Records株式会社のインターンの追加課題です。
## **概要**
クラウドサーバーでブロックチェーンネットワークを構築して、SDKを通じてブロックチェーンのスマートコントラクトを呼び出して、色んな関数を実行できます。

この課題では、Hyperledger Fabricを用いてブロックチェーンネットワークを構築して、Go言語を使ってスマートコントラクトを書いて、JavaでSDKを実現しました。

## **スマートコントラクト**


Hyperledger FabricにはQueryとInvoke二つのメソッドがあります。

*Queryメソッド*

ブロックチェーンに登録したデータを取得する際に呼び出すメソッドです。

*Invokeメソッド*

ブロックチェーンのデータを変更する際（データの作成・更新・削除処理など）に呼び出すメソッドです。

## **関数の説明**

**例：app.Invoke("関数名", new String[]{"para1","para2","para3"});**

**パラメータは決めた順番で入れてください。**

id-唯一の資産番号

name-所有者の名前

point-ポイント数

token-暗号資産トークン数

newrate-新しい交換レート

### **CreateAsset**

資産を作る関数です。パラメータは(id,name,point,token)

例：`app.Invoke("CreateAsset", new String[]{"asset3","Maybe","100","0"});`

### **ReadAsset**

idによって資産の詳細を取得する関数です。パラメータは（id）

例：`app.Query("ReadAsset", new String[]{"asset1"});`

### **UpdateAsset**

資産の更新関数です。パラメータは(id,name,point,token)

例：`app.Invoke("UpdateAsset",new String[]{"asset3","Maybe","150","0"});`

### **DeleteAsset**
idによって資産を削除する関数です。パラメータは(id)

例：`app.Invoke("DeleteAsset",new String[]{"asset3"});`

### **TransferPoint**

ポイントをトークンに交換する関数です。パラメータは(id,point)

例：`app.Invoke("TransferPoint", new String[]{"asset1","10"});`

### **GetAllAssets**
すべての資産を取得する関数です。パラメータはいらないです。

例：`app.Query("GetAllAssets", new String[]{});`

### **GetRate**

今の交換レートを取得する関数です。パラメータもいらないです。

例：`app.Query("GetRate", new String[]{});`

### **SetRate**

交換レートを変更する関数です。パラメータは(newrate)

例：`app.Invoke("SetRate", new String[]{"0.221"});`
