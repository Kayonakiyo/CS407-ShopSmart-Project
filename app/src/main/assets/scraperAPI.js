/**
 * This code is meant to be run on a server and act as an endpoint that runs this
 * puppeteer webdriver. This makes it easier for the Java end of things in the mobile 
 * application to easily 'run' these scripts by sending POST requests to it and letting
 * this script do the work. Requires the packages listed below to work as well as likely
 * port forwarding the IP (or using a domain) to the internet to access.
 */
const express = require("express");
const multer = require("multer");
const puppeteer = require("puppeteer-extra");
const StealthPlugin = require('puppeteer-extra-plugin-stealth');
const UserAgent = require('user-agents');
const app = express();
const port = 3000;
const upload = multer();

var userAgent = new UserAgent();
puppeteer.use(StealthPlugin())
//app.use(express.urlencoded({extended:true}));

app.post("/", upload.none(), async (req, res) => {
    const query = req.body['query']
    chosenStores = JSON.parse(req.body['stores'])
    console.log(query)
    console.log(`Fetching query for ${query}... This may take a while...`)
    console.log("Store list is : " + chosenStores)
    var jsonlist = []

    try{
        console.log("Launching Puppeteer");
        const browser = await puppeteer.launch({headless: false});
        const browser2 = await puppeteer.launch({'headless': 'new'});
        for (const store of chosenStores){
                if(store === 'Amazon'){
                                console.log("Running Amazon Scrape...")
                                const firstRun = await Amazon_Scrape(query,browser);
                                jsonlist = jsonlist.concat(firstRun)
                                console.log("Finished Amazon Scrape!")
                        }
                        if(store === 'UW Bookstore'){
                                        console.log("Running UWBS Scrape...")
                                        const secondRun = await UWBS_Scrape(query, browser2);
                                        jsonlist = jsonlist.concat(secondRun)
                                console.log("Finished UWBS Scrape!")
                }
                if(store === 'Best Buy'){
                        console.log("Running Best Buy Scrape...")
                        const thirdRun = await BestBuyScrape(query, browser)
                        jsonlist = jsonlist.concat(thirdRun)
                        console.log("Finished Best Buy Scrape!")
                }
                if(store === 'Target'){
                 console.log("Running Target Scrape...")
                 const fourthRun = await TargetScrape(query, browser)
                 jsonlist = jsonlist.concat(fourthRun)
                 console.log("Finished Target Scrape!")
                        }
        }

        await browser.close()
        await browser2.close()
        console.log("Returning data...")
        res.json(jsonlist)
    } catch(error){
        console.error("Error:", error);
        res.status(500).send("Internal Server Error");
    }

});

app.listen(port, () => {
    console.log(`Server listening on port ${port}`);
});

const UWBS_Scrape = async (query, browser) => {
    // process query if it has spaces, amazon uses + to concatenate
    if(query !== null && query.split(" ").length > 1){
        let newQuery = "";
        query.split(" ").forEach((word) => newQuery += word + "%20");
        newQuery = newQuery.substring(0, newQuery.length-3); // slice off last %20
        query = newQuery;
    }

    const url = "https://www.uwbookstore.com/merchlist?searchtype=all&txtSearch=" + query.toString().trim();
    //const browser = await puppeteer.launch({ headless: 'false'});
    const page = await browser.newPage();
    await page.setUserAgent(userAgent.random().toString())
    await page.setViewport({ width: 1920, height: 1080 });
    await page.goto(url);
    try{
        await page.waitForSelector('.merchColumn.col-12.grid.merch__card', {timeout: 2000})
        var hasNextPage = true;
        var UWBSItemList = []
        var items = []
                 items = await page.evaluate(() => {
                console.log("current page data :" + document)

                const merchItemDivs = Array.from(document.querySelectorAll('.merchColumn.col-12.grid.merch__card .merchItem.col-6.merch__card-item'));

                var items = []
                merchItemDivs.forEach((item) => {
                        let itemName = item.querySelector('.merchTitle').textContent
                        let itemPrice = item.querySelector('.merchPriceCurrent').textContent
                        itemPrice = itemPrice.substring(itemPrice.indexOf('$')+1);
                        //let itemSKU = item.querySelector('.merchSKU').textContent.toString().trim()
                        let itemImage = item.querySelector('.merchImage').src
                        let itemLink = item.querySelector('.merchLink').href.toString().trim()
                        itemLink = itemLink.substring(0, itemLink.indexOf('&'))
                        items.push({ name: itemName, price: itemPrice, store: "UW Bookstore", onlineLink: itemLink, imageUrl: itemImage })
                });
                return items;
                });

       } catch (error){
                return []
       }


        UWBSItemList = UWBSItemList.concat(items)
            return UWBSItemList;
            // former unused code, just do first page
        var nextPageStatus = await page.$('.chevRight .hover_pointer')
        if(nextPageStatus){
            await page.evaluate(() => {
                document.querySelector('.chevRight .hover_pointer').click();
            });
        } else {
            //await browser.close(); // clean up before returning data
            return UWBSItemList
        }

        // the most important part, wait for this! or the context might not update and be invalid upon next evaluation!
        await page.waitForSelector('.merchColumn.col-12.grid.merch__card')

    }



const Amazon_Scrape = async (query, browser) => {
    // process query if it has spaces, amazon uses + to concatenate
    if(query !== null && query.split(" ").length > 1){
        let newQuery = "";
        query.split(" ").forEach((word) => newQuery += word + "+");
        newQuery = newQuery.substring(0, newQuery.length-1); // slice off last plus
        query = newQuery;
    }

    const url = "https://www.amazon.com/s?k=" + query.toString().trim();
    //const browser = await puppeteer.launch({ headless: false });
    const page = await browser.newPage();
    await page.setUserAgent(userAgent.random().toString())
    await page.setViewport({ width: 1920, height: 1080 });
    await page.goto(url);
    await page.waitForTimeout(3000)
    await page.screenshot({ path: 'amazonsearchresult.png' })

    const items = await page.evaluate(() => {
        console.log("evalutting apge")
        const itemDivs = Array.from(document.querySelectorAll('[data-asin]'))

        var items = []
        itemDivs.forEach((item) => {
        try{
        // get the default num of items (60ish)
            let itemImage = item.querySelector('img[alt]').src
            let itemName = item.querySelector('.a-size-small.a-color-base.a-text-normal').innerText
            let itemPrice = item.querySelector('.a-offscreen').innerText.toString().trim().substring(1)
            let itemLink = item.querySelector('.a-link-normal').href.toString().trim()
            // need to derive item number/sku from the link, ahead of the /dp/xxxxxxxx/
            if(itemLink.includes("sspa")){ // spons item has diff format
                itemLink = itemLink
            } else {
                itemLink = itemLink.substring(0, itemLink.indexOf("/ref"))
            }
            items.push({ name: itemName, price: itemPrice, store: "Amazon", onlineLink: itemLink, imageUrl: itemImage })
        } catch (error){
                console.log("malformed item, skipping")
        }

        })

        return items
    });
    return items

}

const BestBuyScrape = async (query, browser) => {
// process query if it has spaces, amazon uses + to concatenate
if(query !== null && query.split(" ").length > 1){
    let newQuery = "";
    query.split(" ").forEach((word) => newQuery += word + "+");
    newQuery = newQuery.substring(0, newQuery.length-1); // slice off last plus
    query = newQuery;
}

const url = "https://www.bestbuy.com/site/searchpage.jsp?st=" + query.toString().trim();
//const browser = await puppeteer.launch({ headless: false });
const page = await browser.newPage();
await page.setUserAgent(userAgent.random().toString())
await page.setViewport({ width: 1920, height: 1080 });
await page.goto(url);
await page.waitForSelector('.sku-item-list', {timeout:1500})
await page.screenshot({ path: 'bestbuysearchresults.png' })

const items = await page.evaluate(() => {

    const itemDivs = Array.from(document.querySelectorAll('.sku-item'))

    var items = []
    itemDivs.forEach((item) => {
        try{
                // get the default num of items (60ish)
                let itemImage = item.querySelector('.product-image').src
                let itemName = item.querySelector('.sku-title').innerText
                let itemPrice = item.querySelector('.priceView-customer-price').querySelector('span').innerText.toString().trim().substring(1)
                let itemLink = item.querySelector('.sku-title').querySelector('a').href.toString().trim()
                items.push({ name: itemName, price: itemPrice, store: "Best Buy", onlineLink: itemLink, imageUrl: itemImage })
        } catch (error){
                return []
        }
    });

    return items
});
return items

}

const TargetScrape = async (query, browser) => {
// process query if it has spaces, amazon uses + to concatenate
if(query !== null && query.split(" ").length > 1){
    let newQuery = "";
    query.split(" ").forEach((word) => newQuery += word + "+");
    newQuery = newQuery.substring(0, newQuery.length-1); // slice off last plus
    query = newQuery;
}

const url = "https://www.target.com/s?searchTerm=" + query.toString().trim();
//const browser = await puppeteer.launch({ headless: false });
const page = await browser.newPage();
await page.setUserAgent(userAgent.random().toString())
await page.setViewport({ width: 1920, height: 1080 });
await page.goto(url);
await page.waitForSelector('img[alt]', {timeout: 5000})
await page.screenshot({ path: 'targetresults.png' })

const items = await page.evaluate(() => {
    console.log("evalutting apge")
    const itemDivs = Array.from(document.querySelectorAll('.styles__StyledCol-sc-fw90uk-0.dOpyUp'))

    var items = []
    itemDivs.forEach((item) => {
        try{
        // get the default num of items (60ish)
        let itemImage = item.querySelector('img[alt]').src
        let itemName = item.querySelector('img[alt]').alt
        let itemPrice = item.querySelector('[data-test=current-price]').innerText.toString().trim().substring(1)
        let itemLink = item.querySelector('[data-test=product-title]').href.toString().trim()
        if(itemPrice.includes('$')){
        itemPrice = itemPrice.substring(0, itemPrice.indexOf('-')).trim()
        }
        items.push({ name: itemName, price: itemPrice, store: "Target", onlineLink: itemLink, imageUrl: itemImage })
        } catch (error){
            console.log('skipping, malformed item.')
        }
    })

    return items
});
return items

}
