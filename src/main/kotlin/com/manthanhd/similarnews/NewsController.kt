package com.manthanhd.similarnews

import org.apache.commons.lang3.StringEscapeUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.net.URLDecoder
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by manthan on 19/03/17.
 */
class NewsRequest {
    var url: String
    var headline: String

    constructor() {
        this.url = ""
        this.headline = ""
    }

    constructor(url: String, headline: String) {
        this.url = url
        this.headline = headline
    }
}

class SimilarNewsItem {
    var name: String = ""
    var url: String = ""
    var description: String = ""

    constructor() {

    }

    constructor(name: String, url: String, description: String) {
        this.name = name
        this.url = url
        this.description = description
    }
}

class BingNewsItem {
    var name: String = ""
    var url: String = ""
    var description: String = ""

    constructor() {

    }

    constructor(name: String, url: String, description: String) {
        this.name = name
        this.url = url
        this.description = description
    }
}

class BingNewsResponse {
    var value: Array<BingNewsItem> = arrayOf()
    var totalEstimatedMatches: Long = 0

    constructor() {

    }
}

@Service
open class SimilarNewsService {
    val QUERY_URL :String = "https://api.cognitive.microsoft.com/bing/v5.0/news/search?count=1000&q="
    val pattern: Pattern = Pattern.compile("&r=(.*)&")

    val apiKey: String = System.getProperty("apiKey")

    @Autowired
    lateinit var restTemplate: RestTemplate

    fun getSimilarNews(headline: String): ArrayList<SimilarNewsItem> {
        val fqUrl: String = QUERY_URL + headline
        val headers = HttpHeaders();
        headers.set("Ocp-Apim-Subscription-Key", apiKey);

        val requestEntity:HttpEntity<String> = HttpEntity(headers);
        val newsResponseEntity = restTemplate.exchange(fqUrl, HttpMethod.GET, requestEntity, BingNewsResponse::class.java)
        val newsResponse = newsResponseEntity.body
        val similarNewsItems = ArrayList<SimilarNewsItem>()
        for(bingNewsItem in newsResponse.value) {
            val bingUrl = bingNewsItem.url;
            val matcher = pattern.matcher(bingUrl);
            var finalUrl = bingUrl
            if(matcher.find()) {
                finalUrl = matcher.group(1)
                finalUrl = URLDecoder.decode(finalUrl, "UTF-8")
            }

            similarNewsItems.add(SimilarNewsItem(bingNewsItem.name, finalUrl, bingNewsItem.description))
        }
        return similarNewsItems;
    }
}

@RestController
class NewsController {

    @Autowired
    lateinit var similarNewsService: SimilarNewsService

    @RequestMapping(value = "/", method = arrayOf(RequestMethod.POST))
    fun newsController(@RequestBody newsRequest: NewsRequest): ResponseEntity<ArrayList<SimilarNewsItem>> {
        return ResponseEntity.ok(similarNewsService.getSimilarNews(newsRequest.headline));
    }
}