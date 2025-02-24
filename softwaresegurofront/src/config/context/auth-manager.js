export const authManager = (
    state = {signed: false, roleUser: null},
    action 
) => {
    switch (action.type){
        case "SIGNIN":
            return {
                ...state,
                ...action.payload,
                signed: true,
            };
        case "SIGNOUT":
            localStorage.removeItem("user");
            localStorage.removeItem("roleUser");
            return { signed: false, roleUser: null };
        default:
            return state;
    }
    }