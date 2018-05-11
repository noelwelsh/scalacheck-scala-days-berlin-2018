# Extended Exercise: TODO web application

## Endpoints

```
POST /todos?value=get+milk&due=2018-05-13 HTTP/1.1
Host: localhost:8080

HTTP/1.1 201 Created
Location: /todos/68
Content-Type: application/json; charset=UTF-8
...

"/todos/68"
```

```
GET /todos/68
Host: localhost:8080

HTTP/1.1 200 OK
Content-Type: application/json; charset=UTF-8
...

{
  "value": "get milk"
  "due": "2018-05-13"
}
```

```
GET /todos
Host: localhost:8080

HTTP/1.1 200 OK
Content-Type: application/json; charset=UTF-8
...

[
  {
    "value": "get milk"
    "due": "2018-05-13"
  },
  {
    "value": "get cookies"
    "due": "2018-05-12"
  },
  ...
]
```

Dates are [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601#Dates) formatted (`YYYY-MM-DD`).


## Property: Read Your Writes


## Property: Request Validation


## Property: Authentication

`403 Forbidden`

See also: [Github API: Authentication](https://developer.github.com/v3/#authentication)


## Property: Idempotent POSTs


References:

- [Designing robust and predictable APIs with idempotency](https://stripe.com/blog/idempotency)
- [Idempotent Requests](https://stripe.com/docs/api#idempotent_requests) from Stripe
- [What are idempotent and/or safe methods?](http://restcookbook.com/HTTP%20Methods/idempotency/)
- [When should we use PUT and when should we use POST?](http://restcookbook.com/HTTP%20Methods/put-vs-post/)


## Property: Pagination

> Requests that return multiple items will be paginated to 30 items by default. You can specify further pages with the ?page parameter. For some resources, you can also set a custom page size up to 100 with the ?per_page parameter. Note that for technical reasons not all endpoints respect the ?per_page parameter, see events for example.
>
> Note that page numbering is 1-based and that omitting the ?page parameter will return the first page.

From [GitHub API: Pagination](https://developer.github.com/v3/#pagination).


## Property: Filtering


