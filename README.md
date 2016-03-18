# Text2Catalog

Text2Catalog or 'Snap-Books' is a web application targeting mobile devices on all platform. This main idea of the project is to provide the users an on-the-go mechanism to search for the related books based on a text.

Basic steps of the application usage:
- The user clicks the photo of a text. It can be a page from any book, newspaper or magazine. But it should not be handwritten.
  
   OR
   
   The user uploads the photo of a text.
- The user will get book recommendations based on the text scanned.

Behind the scene process:
- The uploaded image undergoes an OCR to extract the text from the image. (OCR library used: Tesseract)
- Alchemy API is used to extract keywords from the entire text.
- Those keywords are used to search the openlibrary based on their relevance.
- User can add a new keyword which they think is important.
- User can also remove an unwanted keyword.
- User can drag and rearrange the priority of the keywords. Books will be rearranged based on the priority

#### Credits
[1] https://openlibrary.org/
