package main

import (
	"log"
	"net/http"
	"net/http/httputil"
	"net/url"
	"os"
	"strings"
	"context"
    	"encoding/json"
"encoding/base64"

    	pb "api-gateway/proto"

    	"google.golang.org/grpc"

)
type TourJsonResponse struct {
	ID             int64                         `json:"id"`
	Name           string                        `json:"name"`
	Description    string                        `json:"description"`
	Difficulty     string                        `json:"difficulty"`
	Price          float64                       `json:"price"`
	Status         string                        `json:"status"`
	AuthorId       int64                         `json:"authorId"`
	DistanceInKm   float64                       `json:"distanceInKm"`
	TransportTimes []*pb.TourTransportTimeGrpcResponse `json:"transportTimes"`
	InShoppingCart bool                          `json:"inShoppingCart"`
	Purchased      bool                          `json:"purchased"`
	AvailableSlots int32 `json:"availableSlots"`
}

func newProxy(target string) *httputil.ReverseProxy {
	if target == "" {
		log.Fatal("Target URL nije postavljen")
	}

	parsedURL, err := url.Parse(target)
	if err != nil {
		log.Fatal("Greška pri parsiranju URL-a:", err)
	}

	proxy := httputil.NewSingleHostReverseProxy(parsedURL)

	proxy.ErrorHandler = func(w http.ResponseWriter, r *http.Request, err error) {
		log.Println("Proxy greška:", err)
		http.Error(w, "Servis trenutno nije dostupan", http.StatusBadGateway)
	}

	return proxy
}

func getEnv(key, fallback string) string {
	value := os.Getenv(key)
	if value == "" {
		return fallback
	}
	return value
}
func extractUserIdFromJwt(r *http.Request) int64 {
	authHeader := r.Header.Get("Authorization")

	if !strings.HasPrefix(authHeader, "Bearer ") {
		return 0
	}

	token := strings.TrimPrefix(authHeader, "Bearer ")
	parts := strings.Split(token, ".")

	if len(parts) < 2 {
		return 0
	}

	payload, err := base64.RawURLEncoding.DecodeString(parts[1])
	if err != nil {
		return 0
	}

	var claims map[string]interface{}
	if err := json.Unmarshal(payload, &claims); err != nil {
		return 0
	}

	if id, ok := claims["id"].(float64); ok {
		return int64(id)
	}

	if userId, ok := claims["userId"].(float64); ok {
		return int64(userId)
	}

	return 0
}
func main() {
	stakeholdersURL := getEnv("STAKEHOLDERS_SERVICE_URL", "http://localhost:8081")
	blogURL := getEnv("BLOG_SERVICE_URL", "http://localhost:8082")
	toursURL := getEnv("TOURS_SERVICE_URL", "http://localhost:8083")
	followerURL := getEnv("FOLLOWER_SERVICE_URL", "http://localhost:5000")
	purchaseURL := getEnv("PURCHASE_SERVICE_URL", "http://localhost:8084")
	port := getEnv("PORT", "8000")
	grpcConn, err := grpc.Dial(
    	"tours-service:9093",
    	grpc.WithInsecure(),
    )

    if err != nil {
    	log.Fatal("Ne mogu da se povežem na tour gRPC servis:", err)
    }

    defer grpcConn.Close()

    tourGrpcClient := pb.NewTourRpcServiceClient(grpcConn)

    stakeholdersGrpcConn, err := grpc.Dial(
        "stakeholders-service:9091",
        grpc.WithInsecure(),
    )

    if err != nil {
        log.Fatal("Ne mogu da se povežem na stakeholders gRPC servis:", err)
    }

    defer stakeholdersGrpcConn.Close()

    authGrpcClient := pb.NewAuthRpcServiceClient(stakeholdersGrpcConn)

	stakeholdersProxy := newProxy(stakeholdersURL)
	blogProxy := newProxy(blogURL)
	toursProxy := newProxy(toursURL)
	followerProxy := newProxy(followerURL)
	purchaseProxy := newProxy(purchaseURL)

	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {

		log.Println("Request:", r.Method, r.URL.Path)
		if (r.URL.Path == "/api/auth/login" || r.URL.Path == "/api/auth/register") && r.Method == "OPTIONS" {
            w.Header().Set("Access-Control-Allow-Origin", "http://localhost:4200")
            w.Header().Set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
            w.Header().Set("Access-Control-Allow-Headers", "Content-Type, Authorization")
            w.WriteHeader(http.StatusOK)
            return
        }
		if (r.URL.Path == "/api/tours/my" || r.URL.Path == "/api/tours/published") && r.Method == "OPTIONS" {
        	w.Header().Set("Access-Control-Allow-Origin", "http://localhost:4200")
        	w.Header().Set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        	w.Header().Set("Access-Control-Allow-Headers", "Content-Type, Authorization")
        	w.WriteHeader(http.StatusOK)
        	return
        }

		if r.URL.Path == "/" {
			w.Write([]byte("API Gateway radi"))
			return
		}

        if r.URL.Path == "/api/auth/register" && r.Method == "POST" {
            var request struct {
                Username string `json:"username"`
                Password string `json:"password"`
                Email    string `json:"email"`
                Role     string `json:"role"`
            }

            if err := json.NewDecoder(r.Body).Decode(&request); err != nil {
                http.Error(w, "Neispravan JSON", http.StatusBadRequest)
                return
            }

            grpcResponse, err := authGrpcClient.Register(
                context.Background(),
                &pb.RegisterGrpcRequest{
                    Username: request.Username,
                    Password: request.Password,
                    Email:    request.Email,
                    Role:     request.Role,
                },
            )

            if err != nil {
                http.Error(w, err.Error(), http.StatusBadRequest)
                return
            }

            w.Header().Set("Access-Control-Allow-Origin", "http://localhost:4200")
            w.Header().Set("Content-Type", "application/json")

            json.NewEncoder(w).Encode(map[string]interface{}{
                "id":       grpcResponse.Id,
                "username": grpcResponse.Username,
                "email":    grpcResponse.Email,
                "role":     grpcResponse.Role,
                "blocked":  grpcResponse.Blocked,
            })

            return
        }

        if r.URL.Path == "/api/auth/login" && r.Method == "POST" {
            var request struct {
                Username string `json:"username"`
                Password string `json:"password"`
            }

            if err := json.NewDecoder(r.Body).Decode(&request); err != nil {
                http.Error(w, "Neispravan JSON", http.StatusBadRequest)
                return
            }

            grpcResponse, err := authGrpcClient.Login(
                context.Background(),
                &pb.LoginGrpcRequest{
                    Username: request.Username,
                    Password: request.Password,
                },
            )

            if err != nil {
                http.Error(w, err.Error(), http.StatusUnauthorized)
                return
            }

            w.Header().Set("Access-Control-Allow-Origin", "http://localhost:4200")
            w.Header().Set("Content-Type", "application/json")

            json.NewEncoder(w).Encode(map[string]interface{}{
                "token": grpcResponse.Token,
                "user": map[string]interface{}{
                    "id":       grpcResponse.User.Id,
                    "username": grpcResponse.User.Username,
                    "email":    grpcResponse.User.Email,
                    "role":     grpcResponse.User.Role,
                    "blocked":  grpcResponse.User.Blocked,
                },
            })

            return
        }

		if strings.HasPrefix(r.URL.Path, "/api/auth") {
			stakeholdersProxy.ServeHTTP(w, r)
			return
		}

		if strings.HasPrefix(r.URL.Path, "/api/users") {
			stakeholdersProxy.ServeHTTP(w, r)
			return
		}

		if strings.HasPrefix(r.URL.Path, "/profile-images/") {
			stakeholdersProxy.ServeHTTP(w, r)
			return
		}

		if strings.HasPrefix(r.URL.Path, "/api/blog") {
			blogProxy.ServeHTTP(w, r)
			return
		}

		if strings.HasPrefix(r.URL.Path, "/api/comments") {
			blogProxy.ServeHTTP(w, r)
			return
		}


if r.URL.Path == "/api/tours/published" {

	touristId := extractUserIdFromJwt(r)

    grpcResponse, err := tourGrpcClient.GetPublishedTours(
    	context.Background(),
    	&pb.GetPublishedToursRequest{
    		TouristId: touristId,
    	},
    )

	if err != nil {
    	w.Header().Set("Access-Control-Allow-Origin", "http://localhost:4200")
    	http.Error(w, err.Error(), http.StatusInternalServerError)
    	return
    }
w.Header().Set("Access-Control-Allow-Origin", "http://localhost:4200")
	w.Header().Set("Content-Type", "application/json")

	response := make([]TourJsonResponse, 0)

    for _, tour := range grpcResponse.Tours {
    	response = append(response, TourJsonResponse{
            ID:             tour.Id,
            Name:           tour.Name,
            Description:    tour.Description,
            Difficulty:     tour.Difficulty,
            Price:          tour.Price,
            Status:         tour.Status,
            AuthorId:       tour.AuthorId,
            DistanceInKm:   tour.DistanceInKm,
            TransportTimes: tour.TransportTimes,
            InShoppingCart: tour.InShoppingCart,
            Purchased:      tour.Purchased,
            AvailableSlots: tour.AvailableSlots,
        })
    }

    json.NewEncoder(w).Encode(response)

	return
}
if r.URL.Path == "/api/tours/my" {

	authorId := extractUserIdFromJwt(r)

	if authorId == 0 {
		http.Error(w, "Nije moguće pročitati userId iz tokena", http.StatusUnauthorized)
		return
	}

	grpcResponse, err := tourGrpcClient.GetMyTours(
		context.Background(),
		&pb.GetMyToursRequest{
			AuthorId: authorId,
		},
	)

	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
w.Header().Set("Access-Control-Allow-Origin", "http://localhost:4200")
	w.Header().Set("Content-Type", "application/json")
	response := make([]TourJsonResponse, 0)

    for _, tour := range grpcResponse.Tours {
    	response = append(response, TourJsonResponse{
            ID:             tour.Id,
            Name:           tour.Name,
            Description:    tour.Description,
            Difficulty:     tour.Difficulty,
            Price:          tour.Price,
            Status:         tour.Status,
            AuthorId:       tour.AuthorId,
            DistanceInKm:   tour.DistanceInKm,
            TransportTimes: tour.TransportTimes,
            InShoppingCart: tour.InShoppingCart,
            Purchased:      tour.Purchased,
            AvailableSlots: tour.AvailableSlots,
        })
    }

    json.NewEncoder(w).Encode(response)

	return
}


if r.URL.Path == "/api/tours/published" {

	touristId := extractUserIdFromJwt(r)

    grpcResponse, err := tourGrpcClient.GetPublishedTours(
    	context.Background(),
    	&pb.GetPublishedToursRequest{
    		TouristId: touristId,
    	},
    )

	if err != nil {
    	w.Header().Set("Access-Control-Allow-Origin", "http://localhost:4200")
    	http.Error(w, err.Error(), http.StatusInternalServerError)
    	return
    }
w.Header().Set("Access-Control-Allow-Origin", "http://localhost:4200")
	w.Header().Set("Content-Type", "application/json")

	response := make([]TourJsonResponse, 0)

    for _, tour := range grpcResponse.Tours {
    	response = append(response, TourJsonResponse{
    		ID:             tour.Id,
    		Name:           tour.Name,
    		Description:    tour.Description,
    		Difficulty:     tour.Difficulty,
    		Price:          tour.Price,
    		Status:         tour.Status,
    		AuthorId:       tour.AuthorId,
    		DistanceInKm:   tour.DistanceInKm,
    		TransportTimes: tour.TransportTimes,
    		InShoppingCart: tour.InShoppingCart,
    		Purchased:      tour.Purchased,
    	})
    }

    json.NewEncoder(w).Encode(response)

	return
}
if r.URL.Path == "/api/tours/my" {

	authorId := extractUserIdFromJwt(r)

	if authorId == 0 {
		http.Error(w, "Nije moguće pročitati userId iz tokena", http.StatusUnauthorized)
		return
	}

	grpcResponse, err := tourGrpcClient.GetMyTours(
		context.Background(),
		&pb.GetMyToursRequest{
			AuthorId: authorId,
		},
	)

	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
w.Header().Set("Access-Control-Allow-Origin", "http://localhost:4200")
	w.Header().Set("Content-Type", "application/json")
	response := make([]TourJsonResponse, 0)

    for _, tour := range grpcResponse.Tours {
    	response = append(response, TourJsonResponse{
    		ID:             tour.Id,
    		Name:           tour.Name,
    		Description:    tour.Description,
    		Difficulty:     tour.Difficulty,
    		Price:          tour.Price,
    		Status:         tour.Status,
    		AuthorId:       tour.AuthorId,
    		DistanceInKm:   tour.DistanceInKm,
    		TransportTimes: tour.TransportTimes,
    		InShoppingCart: tour.InShoppingCart,
    		Purchased:      tour.Purchased,
    	})
    }

    json.NewEncoder(w).Encode(response)

	return
}

		if strings.HasPrefix(r.URL.Path, "/api/tours") {
			toursProxy.ServeHTTP(w, r)
			return
		}

		if strings.HasPrefix(r.URL.Path, "/api/follows") {
			followerProxy.ServeHTTP(w, r)
			return
		}

    if strings.HasPrefix(r.URL.Path, "/api/purchase") {
    	purchaseProxy.ServeHTTP(w, r)
    	return
    }

		http.NotFound(w, r)
	})

	log.Println("API Gateway pokrenut na portu", port)
	err = http.ListenAndServe(":"+port, nil)
	if err != nil {
		log.Fatal("Greška pri pokretanju servera:", err)
	}
}
