package com.prabhas.bookshelfapp.model

data class ImageLinks(
    val smallThumbnail: String? = null,
    val thumbnail: String? = null,
    val small: String? = null,
    val medium: String? = null,
    val large: String? = null,
    val extraLarge: String? = null
)


object SampleImageLinks {
    val example = ImageLinks(
        smallThumbnail = "http://books.google.com/books/publisher/content?id=EPUTEAAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&imgtk=AFLRE734s3CngIs16gM_Ht6GeGF4ew664I7oOGghmfk4pgfFcDYb4GlYCYdjtqqXluL2KUyfq_Ni5MSyv4JxEJ8W679zQ2Ib3okUKau3I1ruqBGrWOt2_haUauWC8sXEgjN7JHm4uOjS&source=gbs_api",
        thumbnail = "http://books.google.com/books/publisher/content?id=EPUTEAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&imgtk=AFLRE71N0ldzv6rliUV_K5ZACa9yPNcT8Ino6YKXJUMje_z4GsC9cp6gFql5TxlmqMoYN3CDhM3XAIO2riFeMXUnFVr5pTLq91htTtG1DDyvOdiR4yI6xu3yEEAn0dRbvNFZ5m7dUC9E&source=gbs_api",
        small = "http://books.google.com/books/publisher/content?id=EPUTEAAAQBAJ&printsec=frontcover&img=1&zoom=2&edge=curl&imgtk=AFLRE71HmTwpoe3KR0AISYk5sDgV2Fz-F-6CDKJtFdvlXSZv3jEzFtsSXGJnEGjtCuoDMxP_6sgP8au1yadB7OmI2MhIBquel7ivcDB8e9ieLyh4HNoXnX3zmxfF_CfIfnNXDv0WHuyA&source=gbs_api",
        medium = "http://books.google.com/books/publisher/content?id=EPUTEAAAQBAJ&printsec=frontcover&img=1&zoom=3&edge=curl&imgtk=AFLRE72LMPH7Q2S49aPeQ3Gm8jLEf6zH4ijuE0nvbOyXBUAgyL816pXzaw0136Pk8jXpfYYFY0IsqL7G7MMDMgKcJhnaoHojWNZpljZmGHeWLL_M7hxkOpmdmO7xza8dfVfPbFmBH4kl&source=gbs_api",
        large = "http://books.google.com/books/publisher/content?id=EPUTEAAAQBAJ&printsec=frontcover&img=1&zoom=4&edge=curl&imgtk=AFLRE71w0J9EOzUzu1O5GMbwhnpI8BLWzOEtzqc9IfyxEDqimZ--H4JlNAZh_1zx8pqPNRf1qDt7FPb57lH5ip-LBlK3zjMC-MCBYcciuoPjTJOFmLv7pp5B6_-UFBap1KRfC0eG7P4d&source=gbs_api",
        extraLarge = "http://books.google.com/books/publisher/content?id=EPUTEAAAQBAJ&printsec=frontcover&img=1&zoom=6&edge=curl&imgtk=AFLRE73t0gcxT-jzEETp8Yo5Osr15nVL7ntKL2WSe2S8kRSio7w0CGgErAq4WbPWIsH4TmOdP_EO6ZoPNSP-YGSOwqfPMw8_IlYE6hy9IKeAs5V_xaHy7drZleF0eizAQiEVg5ci7qby&source=gbs_api"
    )
}