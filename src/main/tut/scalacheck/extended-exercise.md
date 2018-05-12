# Extended Exercise: TODO web application

## Endpoints

```
POST /todos?value=get+milk&due=2018-05-13 HTTP/1.1
Host: localhost:8080

HTTP/1.1 201 Created
Location: /todos/68
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


## Property: Request Validation

* `POST /todos?value=get+milk&due=2018-05-13`
  - `value` parameter is required
  - `due` parameter is optional and must be a ISO 8601 date


## Property: Read Your Writes

* `POST /todos` returns `201 Created`, `GET <Host><Location>` returns correct JSON for initial request parameters

## Property: Authentication

Authentication is implication:

* `Authentication` header present and value is valid

`403 Forbidden`

See also: [Github API: Authentication](https://developer.github.com/v3/#authentication)


## Property: Idempotent POSTs

More than one `POST /todos` request with the same `Idempotency-Key` header
will always return the same `Location` header.

The same key can't be reused with different parameters.

References:

- [Designing robust and predictable APIs with idempotency](https://stripe.com/blog/idempotency)
- [Idempotent Requests](https://stripe.com/docs/api#idempotent_requests) from Stripe
- [What are idempotent and/or safe methods?](http://restcookbook.com/HTTP%20Methods/idempotency/)
- [When should we use PUT and when should we use POST?](http://restcookbook.com/HTTP%20Methods/put-vs-post/)


## Property: Pagination

* `page`: optional, must be > 0, defaults to 1
* `per_page`: optional, must be > 0, defaults to 30

Invariants:

* Given n items, a response will contain `min(n, per_page)` items
* Responses will contain a `first` link if `page` > 1
* Responses will contain a `last` link if ...

See also [GitHub API: Pagination](https://developer.github.com/v3/#pagination).


## Property: Filtering

* `due_by=<ISO8601-date>` query parameter: return only those items who have a `due` property <= `due_by`