# pdfeditor

## Provide easy implementation for Android project
## Below Functionality supported 
 
### 1) PDF Viewer with catching
```
suspend fun getPdfPage(
        resolver: ContentResolver,
        uri: Uri,
        screenWidth: Int,
        iterator: Int,
        slotSize: Int
    ): List<File>
```
### 2) Merge PDF
```
suspend fun mergePdf(
        resolver: ContentResolver,
        dataList: List<Triple<Uri?, String?, Int>>,
        pdfFile: File,
        onProgress: (Float) -> Unit,
    ): Boolean
```

### 3) Split PDF
```
suspend fun splitPdf(
        resolver: ContentResolver,
        srcFile: Uri,
        dstFile: String,
        splitPointList: List<Int>,
        password: String?,
    ): Boolean
```
### 4) Image to PDF
```
suspend fun imageToPdf(
        resolver: ContentResolver,
        uriList: List<Uri>,
        dst: File,
    ): Boolean 
```
### 5) Add Watermark
```
suspend fun addWaterMark(
        resolver: ContentResolver,
        uri: Uri,
        dst: File,
        password: String?,
        imageUri: Uri,
        onProgress: (Float) -> Unit,
    ): Boolean
```
### 6) Add Page Number
```
suspend fun addPageNumber(
        resolver: ContentResolver,
        uri: Uri,
        dst: File,
        password: String?,
        getXYPosition: (Rectangle) -> Pair<Float, Float>,
        onProgress: (Float) -> Unit,
    ): Boolean
```
### 7) Organise PDF
```
suspend fun reOrderPdf(
        resolver: ContentResolver,
        uri: Uri,
        dst: File,
        password: String?,
        pageOrderList: List<Int>,
    ): Boolean
```
### 8) Lock PDF
```
suspend fun setPassword(
        resolver: ContentResolver,
        uri: Uri,
        dst: File,
        password: String,
        prePassword: String?,
    ): Boolean
```
### 9) Unlock PDF
```
suspend fun removePassword(
        resolver: ContentResolver,
        uri: Uri,
        dst: File,
        password: String?,
    ): Boolean

```
### 10) Rotate PDF
```
suspend fun changeOrientation(
        resolver: ContentResolver,
        uri: Uri,
        dst: File,
        value: Int,
        password: String?,
        onProgress: (Float) -> Unit,
    ): Boolean
```

<div align="center">
  <video src="https://github.com/213maheta/pdfeditor/assets/103872646/999ddaed-4897-4d68-8905-a63a8669410d" />
</div>
<div align="center">
  <video src="https://github.com/213maheta/pdfeditor/assets/103872646/2fe341c0-2628-4797-942f-4c97a9886c35" />
</div>
<div align="center">
  <video src="https://github.com/213maheta/pdfeditor/assets/103872646/531fde87-cd52-4e18-9cf1-855791edcf8a" />
</div>
